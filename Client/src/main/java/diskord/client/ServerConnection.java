package diskord.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.client.controllers.Controller;
import diskord.client.controllers.ControllerLogin;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerConnection implements Runnable {
    // Server Connection
    private List<Controller> passiveListeners = new ArrayList<>();
    private HashMap<UUID, Controller> responseWaitingControllers = new HashMap<>();

    private Logger logger = LogManager.getLogger(getClass().getName());
    @Getter
    private Deque<Payload> payloadsToRecive = new ArrayDeque<>();
    @Getter
    private Deque<Payload> payloadsToSend = new ArrayDeque<>();


    // Client UI handling
    private List<Stage> currentlyOpenStages = new ArrayList<>();
    private Stage mainStage;
    @Setter
    private ControllerLogin controllerLogin;

    // Netty
    private Bootstrap bootstrap = new Bootstrap();
    private final ObjectMapper mapper = new ObjectMapper();
    private Channel channel;
    private final ExecutorService pool;
    private final InetSocketAddress address;

    // This should run on a new thread separate from the UI
    public ServerConnection(final InetSocketAddress address, Stage mainStage) {
        this.address = address;
        this.mainStage = mainStage;
        this.pool = Executors.newFixedThreadPool(1);
    }

    @Override
    public void run() {
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(65536, Delimiters.lineDelimiter()));
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast(messageHandler());
            }
        });
        scheduleConnect(10);
    }

    /**
     * Method closes netty connection and server thread.
     * Bye :D
     */
    public void closeNettyAndThread() {
        // Close execution pool so no new connections are made
        pool.shutdownNow();
        //Close bootstrap so no more netty
        channel.close();
        bootstrap.group().shutdownGracefully();
        mainStage.close();
        System.exit(0);

    }

    private void doConnect() {
        try {
            ChannelFuture f = bootstrap.connect(address);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (!future.isSuccess()) {//if is not successful, reconnect
                        future.channel().close();
                        bootstrap.connect(address).addListener(this);
                    } else {//good, the connection is ok
                        channel = future.channel();
                        //add a listener to detect the connection lost
                        addCloseDetectListener(channel);
                        connectionEstablished();
                    }
                }

                private void addCloseDetectListener(Channel channel) {
                    //if the channel connection is lost, the ChannelFutureListener.operationComplete() will be called
                    channel.closeFuture().addListener((ChannelFutureListener) future -> {
                        connectionLost();
                        scheduleConnect(5);
                    });

                }
            });
        } catch (Exception ex) {
            logger.error("doConnect error:" + ex);
            scheduleConnect(1000);
        }
    }

    private void scheduleConnect(int sleepTime) {
        if (pool.isShutdown()) {
            logger.warn("pool is shut down! ScheduleConnect is not fired");
            return;
        }
        logger.info("scheduleConnect");
        Runnable runnableTask = () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(sleepTime);
                doConnect();
            } catch (InterruptedException e) {
                logger.error("Runnable task interrupt exception: " + e);
                // Do nothing because when the task is interrupted, it means serverConnections is being killed
            }
        };
        pool.execute(runnableTask);
    }

    private ChannelHandler messageHandler() {
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
                System.out.printf("received %s%n", msg);
                Payload payload = Payload.fromJson(new ObjectMapper(), (String) msg);
                handlePayload(payload);
            }
        };
    }

    private void connectionLost() {
        logger.warn("Connection lost. Closed UI!");
        killAllAndRestart();
    }

    private void connectionEstablished() {
        logger.info("Connected to server");
        // Create login UI
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            if (controllerLogin == null){
                FXMLLoader loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
                Parent loginRoot = null;
                try {
                    loginRoot = (Parent) loginLoader.load();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                ControllerLogin loginController = (ControllerLogin) loginLoader.getController();
                // When server loses connection, disable login UI using login Controller
                controllerLogin = loginController;
                // Make stage not resizable
                mainStage.setResizable(false);
                // Pass main stage and serverConnection to stage
                loginController.setMainStage(mainStage);
                loginController.setServerConnection(this);
                loginController.init();
                // Add event to main stage that when closed, it will kill netty and this thread
                mainStage.setOnCloseRequest(event -> this.closeNettyAndThread());
                mainStage.setTitle("Login");
                mainStage.setScene(new Scene(loginRoot));
                mainStage.show();
            }
            // Send ping payload to see if server is connected.
            System.out.println(controllerLogin);
            Payload request = new Payload();
            request.setType(PayloadType.BINK);
            writeWithResponse(request, controllerLogin);
        });
    }

    /**
     * Method that sends payload to server and adds listener for server response.
     *
     * @param payload    The payload that is sent to server
     * @param controller The controller that is called when server responds
     * @throws IOException
     */
    public void writeWithResponse(Payload payload, Controller controller){
        logger.info("Payload sent:" + payload.toString());
        responseWaitingControllers.put(payload.getId(), controller);
        write(payload);
    }

    public void write(Payload payload){
        try {
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(payload.toJson(mapper).concat("\n"));
            } else {
                logger.error("Can't send message to inactive connection");
                killAllAndRestart();
            }
        }catch (JsonProcessingException e){
            logger.error("Serverconnection.write: " + e);
            killAllAndRestart();
        }
    }

    /**
     * Method adds controller to passive listener. When server sends payload that
     * was not expected, it will get all controllers that are waiting for that kind of
     * payload types.
     *
     * @param controller Controller that is added to passive listener.
     */
    public void addListener(Controller controller) {
        passiveListeners.add(controller);
    }

    /**
     * Method removes controller from passive listener.
     *
     * @param controller The controller that is removed
     */
    public void removeListeners(Controller controller) {
        passiveListeners.remove(controller);
    }

    /**
     * Method adds stage to server connection. When connection is lost, these
     * stages are closed.
     */
    public void addStage(Stage stage) {
        currentlyOpenStages.add(stage);
    }

    /**
     * Method kills all scenes scenes, controllers and resets connection to server.
     * Opens login UI and starts again
     */
    public void killAllAndRestart() {
        currentlyOpenStages.forEach(x -> Platform.runLater(x::close));
        Platform.runLater(() ->
            // Close all UIs
            controllerLogin.disableUserInteractions("Server connection lost!")
        );

        // Reset all server connection objects
        currentlyOpenStages.clear();
        passiveListeners = new ArrayList<>();
        responseWaitingControllers = new HashMap<>();
    }

    /**
     * Method is called when client has read payload.
     *
     * @param payload The payload that server sent
     * @throws IOException
     */
    public void handlePayload(Payload payload) throws IOException {
        // Check if controller waits for response
        if (responseWaitingControllers.containsKey(payload.getResponseTo())) {
            try {
                Controller controller = responseWaitingControllers.get(payload.getResponseTo());
                controller.handleResponse(payload);
                responseWaitingControllers.remove(payload.getResponseTo());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else { // Controller
            boolean controllerFound = false;
            for (Controller controller : passiveListeners) {
                if (controller.getListenTypes().contains(payload.getType())) {
                    controller.handleResponse(payload);
                    controllerFound = true;
                }
            }
            if (!controllerFound) {
                logger.error("Passive listener not found for payload: " + payload.toString());
            }
        }
    }
}

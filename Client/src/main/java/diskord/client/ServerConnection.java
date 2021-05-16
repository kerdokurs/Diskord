package diskord.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.client.controllers.Controller;

import diskord.client.controllers.ControllerLogin;
import diskord.client.netty.ClientInitializer;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServerConnection implements Runnable {
    // Server Connection
    private List<Controller> passiveListeners = new ArrayList<>();
    private HashMap<UUID, Controller> responseWaitingControllers = new HashMap<>();
    private final InetSocketAddress address;
    private Logger logger = LogManager.getLogger(getClass().getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private Channel channel;
    @Getter
    private Deque<Payload> payloadsToRecive = new ArrayDeque<>();
    @Getter
    private Deque<Payload> payloadsToSend = new ArrayDeque<>();
    private boolean readMessages = true;

    // Client UI handling
    private List<Stage> currentlyOpenStages = new ArrayList<>();
    private Stage mainStage;


    // This should run on a new thread separate from the UI
    public ServerConnection(final InetSocketAddress address, Stage mainStage) {
        this.address = address;
        this.mainStage = mainStage;
    }

    /**
     * Method that sends payload to server and adds listener for server response.
     *
     * @param payload    The payload that is sent to server
     * @param controller The controller that is called when server responds
     * @throws IOException
     */
    public void writeWithResponse(Payload payload, Controller controller) throws IOException {
        //TODO Handle IOException
        logger.info("Payload sent:" + payload.toString());
        responseWaitingControllers.put(payload.getId(), controller);
        write(payload);
    }

    public void write(Payload payload) throws JsonProcessingException {
        channel.writeAndFlush(payload.toJson(mapper).concat("\n"));
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
     * Method that sends payload to server and adds listener for server response.
     * @param payload The payload that is sent to server
     * @param controller The controller that is called when server responds
     * @throws IOException
     */
    public void writeWithResponse(Payload payload, Controller controller) throws IOException {
        //TODO Handle IOException
        listeners2.put(payload.getId(),controller);
        write(payload);
    }

    @Override
    public void run() {
        Platform.setImplicitExit(false);
        final EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer(this, mapper));
            channel = bootstrap.connect(address.getAddress().getHostAddress(), address.getPort()).sync().channel();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        //while (true){
        //    try (final SocketChannel channel = SocketChannel.open(address)) {
        //        logger.info("Connected to server");
        //        // Open UI here
        //
        //        Platform.runLater(() -> {
        //            FXMLLoader loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
        //            Parent loginRoot = null;
        //            try {
        //                loginRoot = (Parent)loginLoader.load();
        //            } catch (IOException err) {
        //                System.out.println("aa");
        //                throw new UncheckedIOException(err);
        //            }
        //            ControllerLogin loginController = (ControllerLogin) loginLoader.getController();
        //            // Make stage not resizable
        //            mainStage.setResizable(false);
        //            // Pass main stage and serverConnection to stage
        //            loginController.setMainStage(mainStage);
        //            loginController.setServerConnection(this);
        //            loginController.init();
        //            mainStage.setTitle("Login");
        //            mainStage.setScene(new Scene(loginRoot));
        //            mainStage.show();
        //        });
        //        readMessages = true;
        //
        //        this.channel = channel;
        //        while (readMessages) {
        //            if(channel.isConnected()){
        //                final Payload payload = read();
        //                if(responseWaitingControllers.containsKey(payload.getResponseTo())){
        //                    responseWaitingControllers.get(payload.getResponseTo()).handleResponse(payload);
        //                    responseWaitingControllers.remove(payload.getResponseTo());
        //                }else{
        //                    //TODO implement passive listener
        //                }
        //            }
        //        }
        //    } catch (final IOException err) {
        //        logger.error("ServerConnection.run: " + err);
        //        killAllAndRestart();
        //    }
        //}
    }

    /**
     * Method kills all scenes scenes, controllers and resets connection to server.
     * Opens login UI and starts again
     */
    public void killAllAndRestart() {

        Platform.runLater(() -> {
            // Close all UIs
            currentlyOpenStages.forEach(Stage::close);
            mainStage.hide();
        });

        // Reset all server connection objects
        currentlyOpenStages.clear();
        passiveListeners = new ArrayList<>();
        responseWaitingControllers = new HashMap<>();
        // just in case signal runnable thread that it needs to close and restart
        readMessages = false;
    }

    public void handlePayload(Payload payload) {
        if (responseWaitingControllers.containsKey(payload.getResponseTo())) {
            try {
                responseWaitingControllers.get(payload.getResponseTo()).handleResponse(payload);
                responseWaitingControllers.remove(payload.getResponseTo());
            } catch (IOException e) {
               throw new UncheckedIOException(e);
            }

        } else {
            //TODO implement passive listener
        }
    }
}

package diskord.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.client.controllers.Controller;
import diskord.client.controllers.ControllerLogin;
import diskord.payload.Payload;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerConnection implements Runnable{
    // Server Connection
    private List<Controller> passiveListeners = new ArrayList<>();
    private HashMap<UUID,Controller> responseWaitingControllers = new HashMap<>();
    private final InetSocketAddress address;
    private Logger logger = LogManager.getLogger(getClass().getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private SocketChannel channel;
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
     * @param payload The payload that is sent to server
     * @param controller The controller that is called when server responds
     * @throws IOException
     */
    public void writeWithResponse(Payload payload, Controller controller) throws IOException {
        //TODO Handle IOException
        logger.info("Payload sent:" + payload.toString());
        responseWaitingControllers.put(payload.getId(),controller);
        write(payload);
    }

    /**
     * Method adds controller to passive listener. When server sends payload that
     * was not expected, it will get all controllers that are waiting for that kind of
     * payload types.
     * @param controller Controller that is added to passive listener.
     */
    public void addListener(Controller controller){
        passiveListeners.add(controller);
    }

    /**
     * Method removes controller from passive listener.
     * @param controller The controller that is removed
     */
    public void removeListeners(Controller controller){
        passiveListeners.remove(controller);
    }

    /**
     * Method adds stage to server connection. When connection is lost, these
     * stages are closed.
     */
    public void addStage(Stage stage){
        currentlyOpenStages.add(stage);
    }

    @Override
    public void run() {
        Platform.setImplicitExit(false);
        while (true){
            try (final SocketChannel channel = SocketChannel.open(address)) {
                logger.info("Connected to server");
                // Open UI here

                Platform.runLater(() -> {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
                    Parent loginRoot = null;
                    try {
                        loginRoot = (Parent)loginLoader.load();
                    } catch (IOException err) {
                        System.out.println("aa");
                        throw new UncheckedIOException(err);
                    }
                    ControllerLogin loginController = (ControllerLogin) loginLoader.getController();
                    // Make stage not resizable
                    mainStage.setResizable(false);
                    // Pass main stage and serverConnection to stage
                    loginController.setMainStage(mainStage);
                    loginController.setServerConnection(this);
                    loginController.init();
                    mainStage.setTitle("Login");
                    mainStage.setScene(new Scene(loginRoot));
                    mainStage.show();
                });
                readMessages = true;

                this.channel = channel;
                while (readMessages) {
                    if(channel.isConnected()){
                        final Payload payload = read();
                        if(responseWaitingControllers.containsKey(payload.getResponseTo())){
                            responseWaitingControllers.get(payload.getResponseTo()).handleResponse(payload);
                            responseWaitingControllers.remove(payload.getResponseTo());
                        }else{
                            //TODO implement passive listener
                        }
                    }
                }
            } catch (final IOException err) {
                logger.error("ServerConnection.run: " + err);
                killAllAndRestart();
            }
        }
    }

    public Payload read() throws IOException {
        // TODO: Add checks if reading is possible
        final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        channel.read(sizeBuffer);
        sizeBuffer.flip();

        final int size = sizeBuffer.getInt();

        final ByteBuffer messageBuffer = ByteBuffer.allocate(size);
        channel.read(messageBuffer);
        messageBuffer.flip();

        final String message = new String(messageBuffer.array());
        logger.info(() -> message);
        Payload response = Payload.fromJson(mapper, message);
        logger.info("response: " + response);
        return response;
    }

    public void write(final Payload payload) throws IOException {
        write(payload.toJson(mapper).concat("\n").getBytes());
    }

    public void write(final byte[] data) throws IOException {
        try{
            final ByteBuffer buffer = ByteBuffer.allocate(data.length);
            buffer.put(data);
            buffer.flip();
            channel.write(buffer);
        }catch (IOException err){
            logger.error("ServerConnection.write: " + err);
            killAllAndRestart();
        }
    }

    /**
     * Method kills all scenes scenes, controllers and resets connection to server.
     * Opens login UI and starts again
     */
    public void killAllAndRestart(){

        Platform.runLater(() ->{
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
}

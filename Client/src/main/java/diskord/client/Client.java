package diskord.client;

import diskord.client.controllers.ControllerLogin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Timer;

public class Client extends Application {
  
  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {

    // Establish connection to server and show login screen
    Timer timer = new Timer();
    ServerConnection serverConnection = new ServerConnection(new InetSocketAddress("localhost",8192), stage, timer);
    Thread serverThread = new Thread(serverConnection);
    serverThread.start();


  }
}

//TODO: clientside implementations of message validity (0 < length <= 255, length ∈ N)
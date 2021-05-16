package diskord.client;

import javafx.application.Application;
import javafx.stage.Stage;
import java.net.InetSocketAddress;


public class Client extends Application {
  
  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage){
    // Establish connection to server and show login screen
    ServerConnection serverConnection = new ServerConnection(new InetSocketAddress("localhost",8192), stage);
    Thread serverThread = new Thread(serverConnection);
    serverThread.start();
  }
}

//TODO: clientside implementations of message validity (0 < length <= 255, length ∈ N)
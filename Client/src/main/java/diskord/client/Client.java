package diskord.client;


import diskord.client.controllers.ControllerLogin;
import diskord.client.controllers.ControllerMain;

import javafx.application.Application;
import javafx.stage.Stage;
import java.net.InetSocketAddress;


public class Client extends Application {
  
  public static void main(final String[] args) {
    launch(args);
  }

  @Override

  public void start(final Stage stage) throws IOException {
    // Establish connection to server
    //TODO fix server client connection
    //ServerConnection serverConnection = new ServerConnection(new InetSocketAddress("localhost",8192));
    //Thread serverThread = new Thread(serverConnection);
    //serverThread.start();

    // Show login screen
    FXMLLoader loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
    Parent loginRoot = (Parent)loginLoader.load();
    ControllerLogin loginController = (ControllerLogin) loginLoader.getController();
    // Pass main stage and serverConnection to stage
    loginController.setMainStage(stage);
    //loginController.setServerConnection(serverConnection);
    loginController.init();
    stage.setTitle("Login");
    stage.setScene(new Scene(loginRoot));
    stage.show();

  }
}

//TODO: clientside implementations of message validity (0 < length <= 255, length ∈ N)
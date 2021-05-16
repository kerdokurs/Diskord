package diskord.client;

import diskord.client.controllers.ControllerLogin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
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
    stage.setResizable(false);
    // Pass main stage and serverConnection to stage
    loginController.setMainStage(stage);
    loginController.setServerConnection(serverConnection);
    loginController.init();
    stage.setTitle("Login");
    stage.setScene(new Scene(loginRoot));
    stage.show();
  }
}

//TODO: clientside implementations of message validity (0 < length <= 255, length ∈ N)
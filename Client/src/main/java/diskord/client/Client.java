package diskord.client;

import diskord.client.controllers.ControllerLogin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Client extends Application {
  
  public static void main(final String[] args) {
    launch(args);

  }

  @Override
  public void start(final Stage stage) throws IOException {
    //TODO Establish connection to server

    // Show login screen
    FXMLLoader loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
    Parent loginRoot = (Parent)loginLoader.load();
    ControllerLogin loginController = (ControllerLogin) loginLoader.getController();
    loginController.setMainStage(stage);
    stage.setTitle("Login");
    stage.setScene(new Scene(loginRoot));
    stage.show();
  }
}
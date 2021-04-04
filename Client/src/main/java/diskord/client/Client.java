package diskord.client;

import diskord.controllers.ControllerMain;
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
    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
    Parent root = (Parent)loader.load();
    ControllerMain controller = (ControllerMain) loader.getController();
    controller.setStage(stage);
    stage.setTitle("Login");
    stage.setScene(new Scene(root));
    stage.show();
  }
}
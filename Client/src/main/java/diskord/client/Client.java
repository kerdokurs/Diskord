package diskord.client;

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
    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));
    stage.setTitle("Login");
    stage.setScene(new Scene(root));
    stage.show();
  }
}
package diskord.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Client extends Application {

    @Override
    public void start(final Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/login.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(final String[] args) {
        launch();
    }

}
package diskord.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerLogin implements Initializable {
    @FXML
    public Button fxButtonSignIn;
    @FXML
    public Button fxButtonRegister;
    @FXML
    public TextField fxTextFieldUsername;
    @FXML
    public TextField fxTextFieldPassword;
    @FXML
    public Label fxLabelLoginErrorMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxLabelLoginErrorMessage.setAlignment(Pos.CENTER);
    }

    /**
     * JavaFX event in Login scene. Method is called when Login button is clicked.
     * Method will attempt to login user. If not possible, it will notify user why login failed.
     * @throws IOException Throws IOException when fxml file can not be loaded
     */
    public void fxEventButtonActionSignIn() throws IOException {
        //              CLIENT LOGIN HERE
        int serverResponse = -1;

        // 0 Login OK
        // 1 User not found
        // 2 Password invalid
        // other
        switch (serverResponse){
            case 0:
                Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Diskord");
                stage.setScene(new Scene(root));
                stage.show();
                return;
            case 1:
                fxLabelLoginErrorMessage.setText("User not found!");
                break;
            case 2:
                fxLabelLoginErrorMessage.setText("Password invalid!");
                break;
            default:
                fxLabelLoginErrorMessage.setText("Other error");
                break;
        }
    }

    /**
     * JavaFX event in Login scene. Method is called when Register button is clicked.
     * Method opens register window.
     * @throws IOException Throws IOException when fxml file can not be loaded
     */
    public void fxEventButtonActionRegister() throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("register.fxml"));
        Stage stage = new Stage();
        stage.setMinWidth(300);
        stage.setMinHeight(320);
        stage.setTitle("Register");
        stage.setScene(new Scene(root));
        stage.show();
    }
}


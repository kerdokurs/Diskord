package diskord.client.controllers;

import diskord.client.ServerConnection;
import diskord.client.TestData;
import diskord.client.payload.Payload;
import diskord.client.payload.PayloadBody;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;


public class ControllerRegister implements Initializable {
    @FXML
    public Button fxButtonRegister;
    @FXML
    public TextField fxTextFieldUsername;
    @FXML
    public TextField fxTextFieldPassword;
    @FXML
    public Label fxLabelPasswordValid;
    @FXML
    public TextField fxTextFieldPasswordConfirm;
    @FXML
    public Label fxLabelMessage;
    @FXML
    private Stage mainStage;

    // Method vars
    ServerConnection serverConnection;
    boolean passwordValid = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxLabelMessage.setAlignment(Pos.CENTER);
    }


    /**
     * Method to set original stage to controller. It is needed when child stages are created so parent stages
     * can be passed on. It creates focus on child stage.
     *
     * @param stage Original stage that is first created.
     */
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    /**
     * Method to pass servers connection to controller.
     *
     * @param serverConnection
     */
    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    /**
     * JavaFX event in Register Scene. Method is called when RegisterScene Button Register
     * is clicked. Method validates user input and then registers account. If account cant be
     * register by server side, it will notify user!
     */
    public void fxEventButtonActionRegister() {
        if (passwordValid) {
            //TODO Register client and get response code
            Payload serverResponse = TestData.getRegisterData();
            switch (serverResponse.getType()) {
                case REGISTER_OK:
                    fxLabelMessage.setText("Account created!");
                    fxLabelMessage.setTextFill(Color.rgb(0, 200, 0));
                    break;
                case REGISTER_ERROR:
                    PayloadBody serverResponseBody = serverResponse.getBody();
                    fxLabelMessage.setText((String)serverResponseBody.get("message"));
                    fxLabelMessage.setTextFill(Color.rgb(200, 0, 0));
                    break;
            }
        } else {
            fxLabelMessage.setText("Account settings are not valid!");
            fxLabelMessage.setTextFill(Color.rgb(200, 0, 0));
        }
    }



    /**
     * JavaFX event in Register Scene. Method is called when key is typed in
     * RegisterScene TextField Password. Method checks if password is valid.
     */
    public void fxEventTextFieldPasswordOnKeyTyped() {
        if (fxTextFieldPassword.getLength() == 0 && fxTextFieldPasswordConfirm.getLength() == 0) {
            fxLabelPasswordValid.setText("");
        } else if (fxTextFieldPassword.getText().equals(fxTextFieldPasswordConfirm.getText())) {
            fxLabelPasswordValid.setText("OK");//
            fxLabelPasswordValid.setTextFill(Color.rgb(0, 200, 0));
            passwordValid = true;
        } else {
            passwordValid = false;
            fxLabelPasswordValid.setText("X");
            fxLabelPasswordValid.setTextFill(Color.rgb(200, 0, 0));
        }
    }

    /**
     * JavaFX event in Register Scene. Method is called when key is typed in
     * RegisterScene TextField PasswordConfirm. Method checks if password is valid.
     */
    public void fxEventTextFieldPasswordConfirmOnKeyTyped() {
        //TODO Sync valid password criterium with server validation
        if (fxTextFieldPasswordConfirm.getText().equals(fxTextFieldPassword.getText()) && fxTextFieldPasswordConfirm.getLength() > 4) {
            fxLabelPasswordValid.setText("OK");//
            fxLabelPasswordValid.setTextFill(Color.rgb(0, 200, 0));
            passwordValid = true;
        } else {
            passwordValid = false;
            if (fxTextFieldPassword.getLength() == 0 && fxTextFieldPasswordConfirm.getLength() == 0) {
                fxLabelPasswordValid.setText("");
            } else {
                fxLabelPasswordValid.setText("X");
                fxLabelPasswordValid.setTextFill(Color.rgb(200, 0, 0));
            }
        }
    }
}



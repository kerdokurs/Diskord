package diskord.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

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
    public Label fxLabelUsernameValid;
    @FXML
    public Label fxLabelPasswordValid;
    @FXML
    public TextField fxTextFieldPasswordConfirm;
    @FXML
    public Label fxLabelMessage;

    boolean passwordValid = false;
    boolean usernameValid = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxLabelMessage.setAlignment(Pos.CENTER);
    }

    /**
     * JavaFX event in Register Scene. Method is called when RegisterScene Button Register
     * is clicked. Method validates user input and then registers account. If account cant be
     * register by server side, it will notify user!
     */
    public void fxEventButtonActionRegister() {
        if(passwordValid && usernameValid){
            //                               CREATON OF CLIENT AND HANDLING OF RESPONSE MESSAGE GOES HERE
            int errorResponseCode = -1;
            boolean userCreated = true;
            if(userCreated){
                fxLabelMessage.setText("Account created!");
                fxLabelMessage.setTextFill(Color.rgb(0,200,0));
            }else{
                switch (errorResponseCode){
                    case -1:
                        fxLabelMessage.setText("Server error response goes here!");
                        fxLabelMessage.setTextFill(Color.rgb(200,0,0));
                        break;
                }
            }
        }else{
            fxLabelMessage.setText("Account settings are not valid!");
            fxLabelMessage.setTextFill(Color.rgb(200,0,0));
        }
    }

    /**
     * JavaFX event in Register Scene. Method is called when key is typed in
     * RegisterScene TextField Username. Method checks if username is available or not.
     */
    public void fxEventTextFieldUsernameOnKeyTyped() {
        // CHECK HERE IF USERNAME IS TAKEN OR NOT!
        boolean usernameAvailable = true;
        if(usernameAvailable){
            usernameValid = true;
            fxLabelUsernameValid.setText("OK");//✔
            fxLabelUsernameValid.setTextFill(Color.rgb(0,200,0));
        }else{
            usernameValid = false;
            fxLabelUsernameValid.setText("X");
            fxLabelUsernameValid.setTextFill(Color.rgb(200,0,0));
        }
    }

    /**
     * JavaFX event in Register Scene. Method is called when key is typed in
     * RegisterScene TextField Password. Method checks if password is valid.
     */
    public void fxEventTextFieldPasswordOnKeyTyped() {
        if(fxTextFieldPassword.getLength() == 0 && fxTextFieldPasswordConfirm.getLength() == 0){
            fxLabelPasswordValid.setText("");
        }else if(fxTextFieldPassword.getText().equals(fxTextFieldPasswordConfirm.getText())){
            fxLabelPasswordValid.setText("OK");// ✔
            fxLabelPasswordValid.setTextFill(Color.rgb(0,200,0));
            passwordValid = true;
        }else{
            passwordValid = false;
            fxLabelPasswordValid.setText("X");
            fxLabelPasswordValid.setTextFill(Color.rgb(200,0,0));
        }
    }

    /**
     * JavaFX event in Register Scene. Method is called when key is typed in
     * RegisterScene TextField PasswordConfirm. Method checks if password is valid.
     */
    public void fxEventTextFieldPasswordConfirmOnKeyTyped() {
        if(fxTextFieldPasswordConfirm.getText().equals(fxTextFieldPassword.getText()) && fxTextFieldPasswordConfirm.getLength() > 4){
            fxLabelPasswordValid.setText("OK");//✔
            fxLabelPasswordValid.setTextFill(Color.rgb(0,200,0));
            passwordValid = true;
        }else{
            passwordValid = false;
            if(fxTextFieldPassword.getLength() == 0 && fxTextFieldPasswordConfirm.getLength() == 0){
                fxLabelPasswordValid.setText("");
            }else{
                fxLabelPasswordValid.setText("X");
                fxLabelPasswordValid.setTextFill(Color.rgb(200,0,0));
            }
        }
    }
}



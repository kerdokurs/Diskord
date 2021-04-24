package diskord.client.controllers;

import diskord.client.ServerConnection;
import diskord.client.TestData;
import diskord.client.User;
import diskord.client.Utils;
import diskord.client.payload.Payload;
import diskord.client.payload.PayloadBody;
import diskord.client.payload.PayloadType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    @FXML
    public CheckBox fxCheckBoxRememberPassword;
    @FXML
    private Stage mainStage;

    // Method vars
    Properties loginProperties;
    ServerConnection serverConnection;

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fxLabelLoginErrorMessage.setAlignment(Pos.CENTER);
        // Check if Diskord folder is created and write login properties to Appdata/diskord
        File diskordDir = new File(System.getenv("APPDATA"),"Diskord");
        diskordDir.mkdir();
        File loginPropertiesFile = new File(diskordDir.getAbsolutePath(), "loginProperties.xml");
        if(!loginPropertiesFile.exists()){
            Utils.writeXmlFromResourcesToFile("loginProperties.xml",loginPropertiesFile);
        }
        // Read properties and set them in UI
        loginProperties = new Properties();
        try{
            loginProperties.loadFromXML(new FileInputStream(loginPropertiesFile));
        }catch (InvalidPropertiesFormatException err){
            // XML file is corrupted. Write it over with template
            loginPropertiesFile.delete();
            Utils.writeXmlFromResourcesToFile("loginProperties.xml",loginPropertiesFile);
            loginProperties.loadFromXML(new FileInputStream(loginPropertiesFile));
        }


        // Check if username is saved
        if(loginProperties.getProperty("saveUsername").equals("True")){
            fxTextFieldUsername.setText(loginProperties.getProperty("username"));
        }

        // Check if password token is saved
        if(loginProperties.getProperty("savePassword").equals("True")){
            //TODO Dont show password token
            fxTextFieldPassword.setText(loginProperties.getProperty("passwordToken"));
            fxCheckBoxRememberPassword.setSelected(true);
        }
    }


    /**
     * Method to set original stage to controller. It is needed when child stages are created so parent stages
     * can be passed on. It creates focus on child stage.
     * @param stage Original stage that is first created.
     */
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    /**
     * Method to pass servers connection to controller.
     * @param serverConnection
     */
    public void setServerConnection(ServerConnection serverConnection){
        this.serverConnection = serverConnection;
    }

    /**
     * JavaFX event in Login scene. Method is called when Login button is clicked.
     * Method will attempt to login user. If not possible, it will notify user why login failed.
     * @throws IOException Throws IOException when fxml file can not be loaded
     */
    public void fxEventButtonActionSignIn() throws IOException {
        //TODO Handle client login here

        //TODO Replace test data
        Payload serverResponse = TestData.getLoginData();
        PayloadBody serverResponseBody = serverResponse.getBody();

        switch (serverResponse.getType()){
            case LOGIN_OK:
                // Save current settings to properties
                loginProperties.setProperty("saveUsername","True");
                loginProperties.setProperty("username",fxTextFieldUsername.getText());

                if(fxCheckBoxRememberPassword.isSelected()){
                    loginProperties.setProperty("savePassword","True");
                    loginProperties.setProperty("passwordToken",(String)serverResponseBody.get("token"));
                }else{
                    loginProperties.setProperty("savePassword","False");
                    loginProperties.setProperty("passwordToken","null");
                }
                // Save properties
                File diskordDir = new File(System.getenv("APPDATA"),"Diskord");
                File loginPropertiesFile = new File(diskordDir.getAbsolutePath(), "loginProperties.xml");
                try(FileOutputStream fileOutputStream = new FileOutputStream(loginPropertiesFile);){
                    loginProperties.storeToXML(fileOutputStream,"");
                }

                // Get current user from server response
                User currentUser = new User(
                        (String)serverResponseBody.get("username"),             // Username
                        UUID.fromString((String)serverResponseBody.get("uuid")),// user UUID
                        (String)serverResponseBody.get("userIconBase64"));      // User icon as base64 string

                // Open main window
                FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
                Parent mainRoot = (Parent)mainLoader.load();
                ControllerMain mainController = (ControllerMain) mainLoader.getController();
                mainController.setMainStage(mainStage);
                mainStage.setTitle("Chat");
                mainStage.setScene(new Scene(mainRoot));
                mainStage.show();
                return;
            case LOGIN_ERROR:
                fxLabelLoginErrorMessage.setText((String)serverResponseBody.get("message"));
                break;
        }
    }

    /**
     * JavaFX event in Login scene. Method is called when Register button is clicked.
     * Method opens register window.
     * @throws IOException Throws IOException when fxml file can not be loaded
     */
    public void fxEventButtonActionRegister() throws IOException {
        Stage registerStage = new Stage();
        registerStage.initModality(Modality.WINDOW_MODAL);
        registerStage.initOwner(mainStage);
        FXMLLoader registerLoader = new FXMLLoader(getClass().getClassLoader().getResource("register.fxml"));
        Parent registerRoot = (Parent)registerLoader.load();
        ControllerRegister controller = (ControllerRegister) registerLoader.getController();
        controller.setMainStage(registerStage);
        registerStage.setTitle("Register");
        registerStage.setScene(new Scene(registerRoot));
        registerStage.show();
    }
}


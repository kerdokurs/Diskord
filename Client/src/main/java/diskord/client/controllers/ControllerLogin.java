package diskord.client.controllers;

import diskord.client.*;
import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.payload.PayloadType;
import javafx.application.Platform;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControllerLogin implements Controller {
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
    public void init() {
        // Add controller to serverConnections;
        //TODO fix server client connection
        //serverConnection.addListener(this);

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
     */
    public void fxEventButtonActionSignIn() throws IOException {
        //Handle client login
        Payload loginPayload = new Payload();
        loginPayload.setType(PayloadType.LOGIN);
        loginPayload.putBody("username",fxTextFieldUsername.getText());
        loginPayload.putBody("password",fxTextFieldPassword.getText());
        serverConnection.write(loginPayload);
    }

    /**
     * JavaFX event in Login scene. Method is called when Register button is clicked.
     * Method opens register window.
     * @throws IOException Throws IOException when fxml file can not be loaded
     */
    public void fxEventButtonActionRegister() throws IOException {
        // Create stage for register window
        Stage registerStage = new Stage();
        // Set registerStage parent to current mainStage, so only registerStage can be clicked
        registerStage.initModality(Modality.WINDOW_MODAL);
        registerStage.initOwner(mainStage);
        // Load fxml
        FXMLLoader registerLoader = new FXMLLoader(getClass().getClassLoader().getResource("register.fxml"));
        Parent registerRoot = (Parent)registerLoader.load();
        ControllerRegister serverController = (ControllerRegister) registerLoader.getController();
        // Pass main stage and serverConnection to new controller
        serverController.setMainStage(registerStage);
        serverController.setServerConnection(serverConnection);
        serverController.init();
        // Remove server controller from serverConnection when stage is closed
        //TODO fix server client connection
        //registerStage.setOnCloseRequest(e ->this.serverConnection.removeListener(serverController));
        registerStage.setTitle("Register");
        registerStage.setScene(new Scene(registerRoot));
        registerStage.show();
    }


    /**
     * Method that handles servers response that is called from ServerConnection method
     * @param response ServersResponse
     * @throws IOException
     */
    @Override
    public void handleResponse(Payload response) throws IOException {
        PayloadBody responseBody = response.getBody();
        switch (response.getType()){
            case LOGIN_OK:
                // Save current settings to properties
                loginProperties.setProperty("saveUsername","True");
                loginProperties.setProperty("username",fxTextFieldUsername.getText());

                if(fxCheckBoxRememberPassword.isSelected()){
                    loginProperties.setProperty("savePassword","True");
                    loginProperties.setProperty("passwordToken",(String)responseBody.get("token"));
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

                //TODO When server is fixed then sync responseBody key values

                // Get current user from server response
                CurrentUser currentUser = new CurrentUser(
                        (String)responseBody.get("username"),             // Username
                        UUID.fromString((String)responseBody.get("uuid")),// user UUID
                        (String)responseBody.get("token"),                // User webtoken
                        (String)responseBody.get("role"),                 // user role
                        (String)responseBody.get("userIconBase64"));      // User icon as base64 string

                // Open main window
                FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
                Parent mainRoot = (Parent)mainLoader.load();
                ControllerMain mainController = (ControllerMain) mainLoader.getController();
                // Pass parameters to controllers
                mainController.setMainStage(mainStage);
                mainController.setCurrentUser(currentUser);
                mainController.setServerConnection(serverConnection);
                mainController.init();
                mainStage.setTitle("Chat");
                mainStage.setScene(new Scene(mainRoot));
                mainStage.show();
                break;
            case LOGIN_ERROR:
                // All FX interaction must be done in this
                Platform.runLater(() -> fxLabelLoginErrorMessage.setText((String)responseBody.get("message")));

                break;
            default:
                // Servers response was not expected
                Platform.runLater(() -> fxLabelLoginErrorMessage.setText("Server sent unrecognisable payload type: " + response.getType()));
                break;
        }
    }

    /**
     * Method that returns set of accepted payloadTypes in controller
     * @return
     */
    @Override
    public Set<PayloadType> getListenTypes() {
        return Stream.of(PayloadType.LOGIN_OK,PayloadType.LOGIN_ERROR)
                .collect(Collectors.toSet());
    }
}


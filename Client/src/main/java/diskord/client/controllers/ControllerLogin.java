package diskord.client.controllers;

import diskord.client.*;
import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.payload.PayloadType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import java.io.*;
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
    private Stage mainStage;

    // Controller objects
    Properties loginProperties;
    ServerConnection serverConnection;

    @SneakyThrows
    public void init() {
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
    }

    /**
     * Method disables login UI elements and shows disable message in UI
     * @param disableMessage String reason for disable that is showed in UI
     */
    public void disableUserInteractions(String disableMessage){
        fxTextFieldUsername.setDisable(true);
        fxTextFieldPassword.setDisable(true);
        fxButtonRegister.setDisable(true);
        fxButtonSignIn.setDisable(true);
        Platform.runLater(() -> {
            fxLabelLoginErrorMessage.setTextFill(Color.color(1, 0, 0));
            fxLabelLoginErrorMessage.setText(disableMessage);
        });
    }

    /**
     * Method enables login UI elements and shows disable message in UI
     * @param enableMessage String reason for enable that is showed in UI
     */
    public void enableUserInteractions(String enableMessage){
        fxTextFieldUsername.setDisable(false);
        fxTextFieldPassword.setDisable(false);
        fxButtonRegister.setDisable(false);
        fxButtonSignIn.setDisable(false);
        Platform.runLater(() -> {
            fxLabelLoginErrorMessage.setTextFill(Color.color(0, 1, 0));
            fxLabelLoginErrorMessage.setText(enableMessage);
        });
    }

    /**
     * JavaFX event in Login scene. Method is called when key is pressed in fxTextFieldPassword or fxTextFieldUsername.
     * Method listens for Enter key to be pressed. After that it will login
     * @param keyEvent Event parameter that states what kind of key was pressed.
     */
    public void fxEventTextFieldOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            fxEventButtonActionSignIn();
        }
    }

    /**
     * JavaFX event in Login scene. Method is called when Login button is clicked.
     * Method will attempt to login user. If not possible, it will notify user why login failed.
     */
    public void fxEventButtonActionSignIn() {
        if(fxTextFieldUsername.getText().length() == 0){
            fxLabelLoginErrorMessage.setTextFill(Color.color(1, 0, 0));
            fxLabelLoginErrorMessage.setText("Username is empty!");
            return;
        }
        if(fxTextFieldPassword.getText().length() == 0){
            fxLabelLoginErrorMessage.setTextFill(Color.color(1, 0, 0));
            fxLabelLoginErrorMessage.setText("Password is empty!");
            return;
        }
        //Handle client login
        Payload loginPayload = new Payload();
        loginPayload.setType(PayloadType.LOGIN);
        loginPayload.putBody("username",fxTextFieldUsername.getText());
        loginPayload.putBody("password",fxTextFieldPassword.getText());
        serverConnection.writeWithResponse(loginPayload,this);
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
        // Make stage not resizable
        registerStage.setResizable(false);
        // Pass main stage, parent controller and serverConnection to new controller
        serverController.setMainStage(mainStage);
        serverController.setServerConnection(serverConnection);
        serverController.setParentController(this);
        serverController.init();
        registerStage.setTitle("Register");
        registerStage.setScene(new Scene(registerRoot));
        registerStage.show();
        // Add newly created stage to serverConnection
        serverConnection.addStage(registerStage);

    }

    /**
     * Method that handles server response. When client sends payload to the server, it will
     * keep track of payload UUID and from what controller the payload is sent. When server
     * responds with payload, the response UUID is used to find the controller that made
     * that payload and handleResponse is called with the servers response payload
     * @param response Payload that server sent
     * @throws IOException Throws IO exception when xml file is not found when loading fxml file
     */
    @Override
    public void handleResponse(Payload response) throws IOException {
        PayloadBody responseBody = response.getBody();
        switch (response.getType()){
            case BONK:
                // Check if UI is disabled.
                // If it is, show recovery message
                if(fxButtonSignIn.isDisable()){
                    enableUserInteractions("Connection restored!");
                }
                break;
            case LOGIN_OK:
                // Save current settings to properties
                loginProperties.setProperty("saveUsername","True");
                loginProperties.setProperty("username",fxTextFieldUsername.getText());
                // Save properties
                File diskordDir = new File(System.getenv("APPDATA"),"Diskord");
                File loginPropertiesFile = new File(diskordDir.getAbsolutePath(), "loginProperties.xml");
                try(FileOutputStream fileOutputStream = new FileOutputStream(loginPropertiesFile);){
                    loginProperties.storeToXML(fileOutputStream,"");
                }

                // Get current user from server response
                CurrentUser currentUser = new CurrentUser(
                        (String)responseBody.get("username"),           // Username
                        UUID.fromString((String) responseBody.get("uuid")),                 // user UUID
                        (String)responseBody.get("token"),              // User webtoken
                        (String)responseBody.get("icon"));              // User icon as base64 string
                Platform.runLater(() -> {
                    // Open main window
                    FXMLLoader mainLoader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
                    Parent mainRoot = null;

                    try {
                        mainRoot = (Parent)mainLoader.load();
                    } catch (IOException e) {
                       throw new UncheckedIOException(e);
                    }
                    ControllerMain mainController = (ControllerMain) mainLoader.getController();
                    // Make stage not resizable
                    mainStage.setResizable(false);
                    // Pass parameters to controllers
                    mainController.setMainStage(mainStage);
                    mainController.setCurrentUser(currentUser);
                    mainController.setServerConnection(serverConnection);
                    mainController.init();
                    mainStage.setTitle("Chat");
                    mainStage.setScene(new Scene(mainRoot));
                    mainStage.show();
                    // ServerConnection login window ui flow management
                    serverConnection.setControllerLogin(null);
                });
                break;
            case LOGIN_ERROR:
                Platform.runLater(() -> {
                    fxLabelLoginErrorMessage.setTextFill(Color.color(1, 0, 0));
                    fxLabelLoginErrorMessage.setText((String)responseBody.get("message"));
                });
                break;
            default:
                // Servers response was not expected
                Platform.runLater(() -> {
                    fxLabelLoginErrorMessage.setTextFill(Color.color(1, 0, 0));
                    fxLabelLoginErrorMessage.setText("Server sent unrecognisable payload type: " + response.getType());
                });
                break;
        }
    }

    /**
     * Method that gets controllers supported listen types.
     * Usually when client sends server payload, client will create UUID and remember
     * from what controller did the request come from so when server responds, it can
     * use the UUID to find the correct controller.
     * But when server sends payload without the UUID, it will filter controllers
     * that have suscribed to listen with those payload types and handle the
     * payload on those controllers.
     * @return Set of supported payload types
     */
    @Override
    public Set<PayloadType> getListenTypes() {
        return Set.of(
                PayloadType.LOGIN_OK,
                PayloadType.LOGIN_ERROR,
                PayloadType.BONK);
    }

    /**
     * Method to set Main stage. It is needed when opening new stage and making javaFX
     * focus the new stage
     * @param mainStage The stage that is set as mainStage
     */
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    /**
     * Method to set parent controller. It is needed when one controller needs to access
     * parent controllers elements
     * @param controller Parent controller
     */
    @Override
    public void setParentController(Controller controller) {
        // Due to login being the first controller, theres no need to set parent controller
    }

    /**
     * Method that sets serversConnection class. It is needed so client can communicate with
     * server and vice versa
     * @param serverConnection The serverConnection class that is set as private serverConnection
     */
    public void setServerConnection(ServerConnection serverConnection){
        this.serverConnection = serverConnection;
    }
}


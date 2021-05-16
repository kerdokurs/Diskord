package diskord.client.controllers;

import diskord.client.ChatFile;

import diskord.client.ChatFileType;

import diskord.client.ServerConnection;
import diskord.client.TestData;
import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.payload.PayloadType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControllerRegisterServer implements Controller{
    @FXML
    public ImageView fxImageViewServerIcon;
    @FXML
    public Button fxButtonSelectIcon;
    @FXML
    public TextField fxTextFieldServerName;
    @FXML
    public TextField fxTextBoxServerDescription;
    @FXML
    public Button fxButtonCreateServer;
    @FXML
    public Label fxLabelServerResponse;

    // Controller objects
    private ControllerMain parentController;
    private ServerConnection serverConnection;
    private Stage mainStage;
    private File serverIconFile;
    private final Logger logger = LogManager.getLogger(getClass().getName());

    public void init(){
        // Align labels text to center
        fxLabelServerResponse.setAlignment(Pos.CENTER);
    }

    /**
     * JavaFX event in Register server scene. Method is called when select icon button is clicked
     * Method opens file explorer and lets user choose icon
     */
    public void fxEventButtonSelectIcon() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        // Allow only supported images to be selected
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter("Images", "*.BMP", "*.GIF", "*.JPEG","*.PNG");
        fileChooser.getExtensionFilters().setAll(fileExtensions);
        fileChooser.setTitle("Select your icon!");
        // Open file chooser in desktop folder
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Desktop"));
        serverIconFile = fileChooser.showOpenDialog(mainStage);
        if(serverIconFile.length() > 1250000){
            fxLabelServerResponse.setText(serverIconFile.getName() + " is larger than 10Mb!");
            return;
        }
        fxImageViewServerIcon.setImage(new Image(new FileInputStream(serverIconFile.getAbsolutePath())));
    }

    /**
     * JavaFX event in Register server scene. Method is called when register button is clicked
     * Method registers server
     */
    public void fxEventButtonCreateServer() throws IOException {
        if(!serverIconFile.exists()){
            fxLabelServerResponse.setTextFill(Color.color(1, 0, 0));
            fxLabelServerResponse.setText("Server icon not found!");
            return;
        }
        if(fxTextFieldServerName.getText().length() == 0){
            fxLabelServerResponse.setTextFill(Color.color(1, 0, 0));
            fxLabelServerResponse.setText("Server name is empty");
            return;
        }else if(fxTextFieldServerName.getText().length() > 50){
            fxLabelServerResponse.setTextFill(Color.color(1, 0, 0));
            fxLabelServerResponse.setText("Server name is too large (max 50)");
            return;
        }
        if(fxTextBoxServerDescription.getText().length() == 0){
            fxLabelServerResponse.setTextFill(Color.color(1, 0, 0));
            fxLabelServerResponse.setText("Server description is empty");
            return;
        }else if(fxTextBoxServerDescription.getText().length() > 100){
            fxLabelServerResponse.setTextFill(Color.color(1, 0, 0));
            fxLabelServerResponse.setText("Server description is too large (Max 100)");
            return;
        }
        // Craft chatFile from server icon
        ChatFile chatFile = new ChatFile(
                UUID.randomUUID(),
                serverIconFile.getName(),

                Base64.getEncoder().encodeToString(Files.readAllBytes(serverIconFile.toPath())),
                ChatFileType.IMAGE);
        // craft payload to server

        Payload request = new Payload();
        request.setJwt(parentController.currentUser.getUserToken());
        request.setType(PayloadType.REGISTER_SERVER);
        request.putBody("icon",chatFile);
        request.putBody("name",fxTextFieldServerName.getText());
        request.putBody("description",fxTextBoxServerDescription.getText());

        serverConnection.writeWithResponse(request,this);

        //TODO Remove test data
        handleResponse(TestData.getServerRegistrationResponse());
    }

    /**
     * Method that handles server response. When client sends payload to the server, it will
     * keep track of payload UUID and from what controller the payload is sent. When server
     * responds with payload, the response UUID is used to find the controller that made
     * that payload and handleResponse is called with the servers response payload
     * @param response Payload that server sent
     * @throws IOException
     */
    @Override
    public void handleResponse(Payload response) throws IOException {
        logger.info(response.toString());
        PayloadBody responseBody = response.getBody();
        // All FX interaction from different thread must be ran later
        switch (response.getType()){
            case REGISTER_SERVER_OK:
                Platform.runLater(() -> fxLabelServerResponse.setTextFill(Color.color(0, 1, 0)));
                Platform.runLater(() -> fxLabelServerResponse.setText("Server created!"));
                // Update user servers
                parentController.getUserSuscribedServers();
                break;
            case REGISTER_SERVER_ERROR:
                Platform.runLater(() -> fxLabelServerResponse.setTextFill(Color.color(1, 0, 0)));
                Platform.runLater(() -> fxLabelServerResponse.setText((String)responseBody.get("message")));
                break;
            default:
                Platform.runLater(() -> fxLabelServerResponse.setTextFill(Color.color(1, 0, 0)));
                Platform.runLater(() -> fxLabelServerResponse.setText("Server sent unrecognisable payload type: " + response.getType()));
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
        return Stream.of(PayloadType.REGISTER_SERVER_OK,PayloadType.REGISTER_SERVER_ERROR)
                .collect(Collectors.toSet());
    }

    /**
     * Method that sets serversConnection class. It is needed so client can communicate with
     * server and vice versa
     * @param serverConnection
     */
    @Override
    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    /**
     * Method to set Main stage. It is needed when opening new stage and making javaFX
     * focus the new stage
     * @param mainStage
     */
    @Override
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
        this.parentController = (ControllerMain) controller;
    }
}

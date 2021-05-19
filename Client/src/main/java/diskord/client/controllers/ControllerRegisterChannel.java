package diskord.client.controllers;

import diskord.client.ChatFile;

import diskord.client.ChatFileType;

import diskord.client.ServerConnection;
import diskord.client.controllers.listview.ListViewServerRow;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

public class ControllerRegisterChannel implements Controller{
    @FXML
    public ImageView fxImageViewChannelIcon;
    @FXML
    public Button fxButtonSelectIcon;
    @FXML
    public TextField fxTextFieldChannelName;
    @FXML
    public Button fxButtonCreateChannel;
    @FXML
    public Label fxLabelChannelResponse;

    // Controller objects
    private ControllerMain parentController;
    private ServerConnection serverConnection;
    private Stage mainStage;
    private File channelIconFile;
    private final Logger logger = LogManager.getLogger(getClass().getName());


    public void init(){
        // Align labels text to center
        fxLabelChannelResponse.setAlignment(Pos.CENTER);
    }

    /**
     * JavaFX event in Register channel scene. Method is called when select icon button is clicked
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
        channelIconFile = fileChooser.showOpenDialog(mainStage);
        if(channelIconFile.length() > 1250000){
            fxLabelChannelResponse.setText(channelIconFile.getName() + " is larger than 10Mb!");
            return;
        }
        fxImageViewChannelIcon.setImage(new Image(new FileInputStream(channelIconFile.getAbsolutePath())));
    }

    /**
     * JavaFX event in Register channel scene. Method is called when key is pressed in fxTextField
     * Method listens for Enter key to be pressed. After that it will create server
     * @param keyEvent Event parameter that states what kind of key was pressed.
     */
    public void fxEventTextFieldOnKeyPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            fxEventButtonCreateChannel();
        }
    }

    /**
     * JavaFX event in Register channel scene. Method is called when register button is clicked
     * Method registers channel
     */
    public void fxEventButtonCreateChannel() throws IOException {
        if(!channelIconFile.exists()){
            fxLabelChannelResponse.setTextFill(Color.color(1, 0, 0));
            fxLabelChannelResponse.setText("Channel icon not found!");
            return;
        }
        if(fxTextFieldChannelName.getText().length() == 0){
            fxLabelChannelResponse.setTextFill(Color.color(1, 0, 0));
            fxLabelChannelResponse.setText("Channel name is empty");
            return;
        }else if(fxTextFieldChannelName.getText().length() > 50){
            fxLabelChannelResponse.setTextFill(Color.color(1, 0, 0));
            fxLabelChannelResponse.setText("Channel name is too large (max 50)");
            return;
        }

        // craft payload to server
        final ListViewServerRow selectedItem = parentController.fxListViewServers.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            Payload request = new Payload();
            request.setJwt(parentController.currentUser.getUserToken());
            request.setType(PayloadType.REGISTER_CHANNEL);
            request.putBody("icon",Base64.getEncoder().encodeToString(Files.readAllBytes(channelIconFile.toPath())));
            request.putBody("name",fxTextFieldChannelName.getText());
            request.putBody("server_id", selectedItem.getUuid());
            serverConnection.writeWithResponse(request,this);
        }
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
            case REGISTER_CHANNEL_OK:
                Platform.runLater(() -> fxLabelChannelResponse.setTextFill(Color.color(0, 1, 0)));
                Platform.runLater(() -> fxLabelChannelResponse.setText("Channel created!"));

                Payload request = new Payload();
                request.setType(PayloadType.INFO_CHANNELS);
                request.putBody("server_id", parentController.fxListViewServers.getSelectionModel().getSelectedItem().getUuid());
                request.setJwt(parentController.currentUser.getUserToken());
                serverConnection.writeWithResponse(request, parentController);
                break;
            case REGISTER_CHANNEL_ERROR:
                Platform.runLater(() -> fxLabelChannelResponse.setTextFill(Color.color(1, 0, 0)));
                Platform.runLater(() -> fxLabelChannelResponse.setText((String)responseBody.get("message")));
                break;
            default:
                Platform.runLater(() -> fxLabelChannelResponse.setTextFill(Color.color(1, 0, 0)));
                Platform.runLater(() -> fxLabelChannelResponse.setText("Channel sent unrecognisable payload type: " + response.getType()));
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
        return Stream.of(PayloadType.REGISTER_CHANNEL_OK,PayloadType.REGISTER_CHANNEL_ERROR)
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

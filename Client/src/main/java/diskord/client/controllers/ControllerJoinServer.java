package diskord.client.controllers;

import diskord.client.ServerConnection;
import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.payload.PayloadType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;

public class ControllerJoinServer implements Controller {
    @FXML
    public TextField fxTextFieldServerID;
    @FXML
    public Button fxButtonJoinServer;
    @FXML
    public Label fxLabelMessage;

    // Controller objects
    private ServerConnection serverConnection;
    private ControllerMain parentController;

    @Override
    public void init() {
        // Set label text center
        fxLabelMessage.setAlignment(Pos.CENTER);
    }

    @Override
    public void handleResponse(Payload response){
        PayloadBody responseBody = response.getBody();
        switch (response.getType()){
            case JOIN_SERVER_OK:
                Platform.runLater(() -> {
                    fxLabelMessage.setTextFill(Color.color(0, 1, 0));
                    fxLabelMessage.setText("Joined server");
                });
                parentController.getUserSubscribedServers();
                break;
            case JOIN_SERVER_ERROR:
                Platform.runLater(() -> {
                    fxLabelMessage.setTextFill(Color.color(1, 0, 0));
                    fxLabelMessage.setText((String) responseBody.get("message"));
                });

                break;
            default:
                Platform.runLater(() -> {
                    fxLabelMessage.setText("Server sent unrecognisable payload type: " + response.getType());
                    fxLabelMessage.setTextFill(Color.rgb(200, 0, 0));
                });
                break;
        }
    }

    /**
     * JavaFX event in JoinServer scene. Method is called when key is pressed in fxTextField
     * Method listens for Enter key to be pressed. After that it will join server
     * @param keyEvent Event parameter that states what kind of key was pressed.
     */
    public void fxEventTextFieldOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            fxEventButtonJoinServer();
        }
    }

    @Override
    public Set<PayloadType> getListenTypes() {
        return null;
    }

    @Override
    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void setMainStage(Stage mainStage) {
        // This controller does not use main stage for anything
    }

    @Override
    public void setParentController(Controller controller) {
        this.parentController = (ControllerMain) controller;
    }

    /**
     * JavaFX event  in Join server scene. Method is called when Join server is clicked.
     * Method joins user to server
     */
    public void fxEventButtonJoinServer(){
        if(fxTextFieldServerID.getText().length() == 0){
            fxLabelMessage.setTextFill(Color.color(1, 0, 0));
            fxLabelMessage.setText("ID is empty!");
            return;
        }
        Payload request = new Payload();
        request.setJwt(parentController.currentUser.getUserToken());
        request.setType(PayloadType.JOIN_SERVER);
        request.putBody("join_id", fxTextFieldServerID.getText());

        serverConnection.writeWithResponse(request,this);
    }
}

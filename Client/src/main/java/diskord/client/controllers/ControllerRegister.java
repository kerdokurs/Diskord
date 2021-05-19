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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ControllerRegister implements Controller{
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
    public ImageView fxImageViewUserIcon;
    @FXML
    public Button fxButtonSelectIcon;

    // Method vars
    private Stage mainStage;
    private ServerConnection serverConnection;
    private boolean passwordValid = false;
    private File userIconFile;

    public void init(){
        // This controller does not need active listener from server connection
        fxLabelMessage.setAlignment(Pos.CENTER);
    }

    /**
     * JavaFX event in Register Scene. Method is called when RegisterScene Button Register
     * is clicked. Method validates user input and then registers account. If account cant be
     * register by server side, it will notify user!
     */
    public void fxEventButtonActionRegister(){
        if(fxTextFieldUsername.getText().length() < 4){
            fxLabelMessage.setTextFill(Color.color(1, 0, 0));
            fxLabelMessage.setText("Username must be greater than 3!");
            return;
        }
        if(userIconFile == null || !userIconFile.exists()){
            fxLabelMessage.setTextFill(Color.color(1, 0, 0));
            fxLabelMessage.setText("User icon not found!");
            return;
        }
        if (passwordValid) {
            // Register client and get response code
            Payload request = new Payload();
            request.setType(PayloadType.REGISTER);
            request.putBody("username",fxTextFieldUsername.getText());
            request.putBody("password",fxTextFieldPassword.getText());
            try {
                request.putBody("icon",Base64.getEncoder().encodeToString(Files.readAllBytes(userIconFile.toPath())));
            } catch (IOException e) {
                fxLabelMessage.setTextFill(Color.color(1, 0, 0));
                fxLabelMessage.setText("User icon not usable! Choose another");
                return;
            }
            serverConnection.writeWithResponse(request,this);
        } else {
            fxLabelMessage.setText("Account settings are not valid!");
            fxLabelMessage.setTextFill(Color.rgb(200, 0, 0));
        }
    }

    /**
     * JavaFX event in Register Scene. Method is called when select icon button
     * is clicked. Method sets user selected icon to image view and stores the icon.
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
        userIconFile = fileChooser.showOpenDialog(mainStage);
        if(userIconFile.length() > 1250000){
            fxLabelMessage.setText(userIconFile.getName() + " is larger than 10Mb!");
            return;
        }
        fxImageViewUserIcon.setImage(new Image(new FileInputStream(userIconFile.getAbsolutePath())));
    }

    /**
     * JavaFX event in register scene. Method is called when key is pressed in TextFields
     * Method listens for Enter key to be pressed. After that it will register.
     * @param keyEvent Event parameter that states what kind of key was pressed.
     */
    public void fxEventTextFieldOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            fxEventButtonActionRegister();
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

    /**
     * Method that handles servers response that is called from ServerConnection method
     * @param response ServersResponse
     */
    @Override
    public void handleResponse(Payload response){
        switch (response.getType()){
            case REGISTER_OK:
                Platform.runLater(() -> {
                    fxLabelMessage.setText("Account created!");
                    fxLabelMessage.setTextFill(Color.rgb(0, 200, 0));
                });

                break;

            case REGISTER_ERROR:
                Platform.runLater(() -> {
                    PayloadBody serverResponseBody = response.getBody();

                    fxLabelMessage.setTextFill(Color.rgb(200, 0, 0));
                    fxLabelMessage.setText((String)serverResponseBody.get("message"));


                });
                break;

            default:
                Platform.runLater(() -> {



                });
                break;
        }
    }
    /**
     * Method that returns set of accepted payloadTypes in controller
     * @return
     */
    @Override
    public Set<PayloadType> getListenTypes() {
        return Stream.of(PayloadType.REGISTER_OK,PayloadType.REGISTER_ERROR)
                .collect(Collectors.toSet());
    }

    /**
     * Method to set original stage to controller. It is needed when child stages are created so parent stages
     * can be passed on. It creates focus on child stage.
     *
     * @param mainStage Original stage that is first created.
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
        // Due to no need to access parent controller, do nothing
    }

    /**
     * Method to pass servers connection to controller.
     *
     * @param serverConnection
     */
    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
}



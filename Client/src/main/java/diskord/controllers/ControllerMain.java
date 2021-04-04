package diskord.controllers;

import diskord.client.ChatFile;
import diskord.client.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.SneakyThrows;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

public class ControllerMain implements Initializable {

    // FXML gui elements
    @FXML
    public ListView<listViewServerRow> fxListViewServers;
    @FXML
    public ListView<listViewChatRow> fxListViewChat;
    @FXML
    public Label fxLabelChatStatus;
    @FXML
    public Button fxButtonChatSend;
    @FXML
    public Button fxButtonChatAddFile;
    @FXML
    public TextArea fxTextAreaChatBox;
    @FXML
    public Label fxLabelServerStatus;
    @FXML
    public SplitPane fxSplitPane;
    @FXML
    ObservableList<listViewServerRow> listViewServerData = FXCollections.observableArrayList();
    @FXML
    ObservableList<listViewChatRow> listViewChatData = FXCollections.observableArrayList();
    @FXML
    private Stage stage;

    // Controller objects
    File attachedFile;
    User currentUser;

    /**
     * Init method for ControllerMain
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set current client instance
        //TODO Get current user UUID from server
        currentUser = new User("test user", UUID.fromString("bf867bfc-9541-11eb-a8b3-0242ac130003"));

        //TODO Get current server all profile icons

        // Check if Diskord cache folder exists
        File diskordDir = new File(System.getenv("APPDATA"),"Diskord");
        diskordDir.mkdir();
        new File(diskordDir, "userIcons").mkdir();

        //TODO Get all user profile icons from server

        // GET ALL PROFILE ICONS FROM SERVER CODE GOES HERE
        // Set ObservableList of custom listViewServerRows to fxListViewServer. This way when item is added to
        // observable list, it gets added to UI
        fxListViewServers.setItems(listViewServerData);
        // Create callback so when item is added, it will handle the new item with custom cell factory.
        fxListViewServers.setCellFactory(listView -> new CustomServerListViewCell());

        // Set ObservableList of custom listViewChatRows to fxListViewChat. This way when item is added to
        // observable list, it gets added to UI
        fxListViewChat.setItems(listViewChatData);
        // Create callback so when item is added, it will handle the new item with custom cell factory.
        fxListViewChat.setCellFactory(listView -> new CustomChatListViewCell());
        // Set Server and Client Listview not selectable.
        fxListViewServers.setFocusTraversable(false);
        fxListViewChat.setFocusTraversable(false);

        // Set fxTextAreaChatBox max character limit
        fxTextAreaChatBox.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 100 ? change : null));

        // Set fxTextAreaChatBox text to wrap
        fxTextAreaChatBox.setWrapText(true);
    }

    /**
     * Method to set original stage to controller. It is needed when child stages are created so parent stages
     * can be passed on. It creates focus on child stage.
     * @param stage Original stage that is first created.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void handleChatMessage(User currentUser, String message, String timeStamp, int dataType, ChatFile file){
            listViewChatData.add(
                    new listViewChatRow(
                            currentUser,
                            message,
                            timeStamp,
                            dataType,
                            file));
    }
    /**
     * JavaFX event in Main scene. Method is called when Send button is clicked.
     * Method will send message to currently open chat.
     */
    public void fxEventButtonSendChat() throws IOException {
        // Check if there is any text
        if(fxTextAreaChatBox.getLength() == 0 && attachedFile == null){
            return;
        }
        // Get user message
        String message = fxTextAreaChatBox.getText();
        // Strip unnecessary new lines from start and end of message
        char[] messageCharArray = message.toCharArray();
        for (int i = 0; i < messageCharArray.length; i++){
            if(messageCharArray[i] != '\n'){
                message = message.substring(i);
                break;
            }
        }
        for (int i = messageCharArray.length - 1; 0 <= i; i--){
            if(messageCharArray[i] != '\n'){
                message = message.substring(0,i + 1);
                break;
            }
        }


        String fileBase64;
        // Read user attached file to base64
        //if(attachedFile != null){
        //    byte[] bytes = new byte[(int)attachedFile.length()];
        //    try(FileInputStream fis = new FileInputStream(attachedFile)){
        //        fis.read(bytes);
        //    }catch (IOException err){
        //        attachedFile = null;
        //        fxTextAreaChatBox.setText(attachedFile.getName() + " not found!");
        //        return;
        //    }
        //    fxLabelChatStatus.setText("");
        //    //fileBase64 =  Base64.getEncoder().encodeToString(bytes);
        //}
        //TODO send message to server. Get response code

        // Clear currently written text
        fxTextAreaChatBox.setText("");
        fxLabelChatStatus.setText("");
        // Add written message to listviewChat
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime currentDate = LocalDateTime.now();

        if(attachedFile == null){ // Only message. Datatype = 0
            handleChatMessage(currentUser,message,dateTimeFormatter.format(currentDate),0,null);
            return;
        }
        try{
            ChatFile chatFile = new ChatFile(null,attachedFile.getName(),Base64.getEncoder().encodeToString(Files.readAllBytes(attachedFile.toPath())));
            // Get file extension
            int extensionIndex = attachedFile.getName().lastIndexOf('.');
            String extension = attachedFile.getName().substring(extensionIndex+1).toLowerCase(Locale.ROOT);
            // Check if file is java.Image supported image type
            if(extension.equals("png") || extension.equals("gif") || extension.equals("jpeg") || extension.equals("bmp")) {
                // Message with image. Datatype = 1
                handleChatMessage(currentUser, message, dateTimeFormatter.format(currentDate), 1, chatFile);
            }else{
                // Message with file. Datatype = 2
                handleChatMessage(currentUser, message, dateTimeFormatter.format(currentDate), 2, chatFile);
            }
        }catch (IOException err){
            fxTextAreaChatBox.setText(attachedFile.getName() + " not found!");
        }
        // All actions done with attached file. Set it to null pointer
        attachedFile = null;

    }

    /**
     *  JavaFX event in Main scene. Method is called when add file button is clicked.
     *  Method will add file to message
     */
    public void fxEventButtonAddFile(){

        // Open file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select your file!");
        // Open file chooser in desktop folder
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Desktop"));
        attachedFile = fileChooser.showOpenDialog(stage);
        if(attachedFile.length() > 1250000){
            fxLabelChatStatus.setText(attachedFile.getName() + " is larger than 10Mb!");
            attachedFile = null;
            return;
        }
        // Check if file is selected
        if(attachedFile != null){
            fxLabelChatStatus.setText(attachedFile.getName() + " selected!");
        }else{
            fxLabelChatStatus.setText("");
        }
    }

    //TODO Handle changing of servers
    public void fxEventListViewServerOnMouseClicked() {

        var selectedItem = fxListViewServers.getSelectionModel().getSelectedItem();
        // Check if nothing has been selected
        if(selectedItem == null){
            return;
        }
        // Get chatID so you can request messages from server
        Integer chatID = selectedItem.getChatID();

        // SEND SERVER THAT CLIENT REQUESTS CHAT CONTENT
        // SERVER WILL SEND JSON PARSED MESSAGES

    }

    /**
     * JavaFX event in Main scene. Method is called when key is pressed in fxTextAreaChatBox.
     * Method listens for Enter key to be pressed. After that it will send current message
     * @param keyEvent Event parameter that states what kind of key was pressed.
     */
    public void fxEventTextAreaOnKeyPressedChat(KeyEvent keyEvent) throws IOException {
       // Check if shift is also pressed. If it is, add new line.
       if (keyEvent.getCode() == KeyCode.ENTER && keyEvent.isShiftDown()){
           fxTextAreaChatBox.appendText("\n");
       }else if(keyEvent.getCode() == KeyCode.ENTER){
           // Due to enter adding new line to text area, fxEventButtonSendChat will send new line.
           // Remove last char that is new line from text area
           String message = fxTextAreaChatBox.getText();
           fxTextAreaChatBox.setText(message.substring(0, message.length() - 1));
           fxEventButtonSendChat();
       }
    }

    /**
     * JavaFX event in Main Scene. Method is called when Message Image is cliced in fxListViewChat.
     * Method creates new scene with image. Allows saving of image in original quality.
     */
    public void fxEventListViewChatMessageImageOnMouseClicked() {
        // Get the image
        ChatFile chatFile = fxListViewChat.getSelectionModel().getSelectedItem().getFile();
        Image image = chatFile.getImage(0);
        // Create new stage where to show image
        Stage imageStage = new Stage();
        imageStage.initOwner(stage);
        imageStage.initStyle(StageStyle.UNDECORATED);
        // Create new pane and  set padding
        StackPane pane = new StackPane();
        pane.setPadding(new Insets(10,10,10,10));
        // Create imageView where image is shown
        ImageView imageView = new ImageView(image);
        // Create label that shows image name
        Label labelImageName = new Label();
        labelImageName.setText(chatFile.getFileName());
        // Create button that will save image and add eventlistener for saving image
        Button buttonSaveImage = new Button();
        buttonSaveImage.setText("Save image");
        buttonSaveImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @SneakyThrows
            @Override
            public void handle(MouseEvent event) {
                // Choose folder where to save
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select where to save");
                // Open file chooser in desktop folder
                directoryChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Desktop"));
                File chosenDirectory = new File(directoryChooser.showDialog(imageStage),chatFile.getFileName());
                // Write the file to folder
                byte[] img = Base64.getDecoder().decode(chatFile.getBase64File());
                try (FileOutputStream fos = new FileOutputStream(chosenDirectory)) {
                    fos.write(img);
                }
            }
        });

        // Create button with event listener so when clicked, it will close stage
        Button buttonCloseStage = new Button();
        buttonCloseStage.setText("X");
        buttonCloseStage.setOnMouseClicked(event -> imageStage.close());
        // Spacers for HBox
        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);
        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);
        // Set elements to scene
        HBox hBox = new HBox(buttonCloseStage,region1, labelImageName,region2, buttonSaveImage);
        hBox.setSpacing(5);
        VBox vBox = new VBox(hBox,imageView);
        vBox.setSpacing(5);
        pane.getChildren().add(vBox);
        imageStage.setScene(new Scene(pane));
        imageStage.show();
    }

    /**
     * Custom listViewRow class data class. This class holds servers name, description and image.
     */
    private static class listViewServerRow {
        private Image image;
        private String serverName;
        private String description;
        private Integer chatID;
        public  Integer getChatID(){
            return chatID;
        }
        public String getServerName() {
            return serverName;
        }
        public String getDescription() {
            return description;
        }
        public Image getImage(){
            return image;
        }

        /**
         * Custom listview row object for server listview.
         * @param name Server name
         * @param description Server description
         * @param image Server icon
         * @param serverID Server
         */
        public listViewServerRow(String name, String description,Image image, Integer serverID) {
            super();
            this.chatID = serverID;
            this.serverName = name;
            this.description = description;
            this.image = image;
        }
    }

    /**
     * Custom Listview Listcell. It holds the content of single ListViewRow
     */
    private class CustomServerListViewCell extends ListCell<listViewServerRow> {
        private HBox content;
        private ImageView imageView;
        private Text name;
        private Text description;

        /**
         * Contstructor for CustomListCell. Define variables and set the layout of single cell
         */
        public CustomServerListViewCell() {
            super();
            name = new Text();
            description = new Text();
            imageView = new ImageView();
            VBox vBox = new VBox( name, description);
            content = new HBox(imageView,vBox);
            content.setSpacing(10);
        }

        @Override
        protected void updateItem(listViewServerRow item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) { // <== test for null item and empty parameter
                name.setText(item.getServerName());
                description.setText(item.getDescription());
                imageView.setImage(item.getImage());
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * Custom Listview Listcell. It holds the content of single ListViewRow
     */
    private static class listViewChatRow {
        @Getter
        private final User user; // Get username and icon from user object
        @Getter
        private final String message;
        @Getter
        private final String timestamp; // The date when message was sent
        @Getter
        private final int dataType;
        @Getter
        private final ChatFile file;
        /**
         * Custom listview row object for chat listview.
         * @param user User object that has UUID, name and icon
         * @param message Message data
         * @param timestamp Date when message was sent
         * @param dataType Datatype that indicates what kind of functionality listviewChatRow has. 0 = only message
         *                 1 = message and image. Shows image and message in listview
         *                 2 = file. Shows image icon and file name. Also indicates that when clicked on, will
         *                 download the file from server
         * @param file     ChatFile object that has file name,base64 and UUID. UUID is necessary so file can be
         *                 requested from server with UUID
         */
        public listViewChatRow(User user, String message, String timestamp, int dataType, ChatFile file) {
            super();
            this.user = user;
            this.message = message;
            this.timestamp = timestamp;
            this.dataType = dataType;
            this.file = file;
        }
    }

    /**
     * Custom Listview Listcell. It holds the content of single ListViewRow
     */
    private class CustomChatListViewCell extends ListCell<listViewChatRow> {
        private final HBox content;
        private final ImageView imageViewUserIcon; // Profile icon
        private final Text textUsername;           // Username
        private final Text textMessage;            // Text
        private final Text textSentDate;           // Sent text
        private final ImageView messageImage;      // Image in message, if there is


        /**
         * Contstructor for CustomListCell. Define variables and set the layout of single cell
         */
        public CustomChatListViewCell() {
            super();
            // Create instances
            textUsername = new Text();
            textMessage = new Text();
            textSentDate = new Text();
            imageViewUserIcon = new ImageView();
            messageImage = new ImageView();
            // Add event listener so when image is clicked, new scene opens with the image
            messageImage.setOnMouseClicked(event -> fxEventListViewChatMessageImageOnMouseClicked());

            // Set message text wrapping, so scroll bar doesnt appear
            textMessage.wrappingWidthProperty().bind(fxListViewChat.widthProperty().subtract(70));

            // Set content and its layout
            HBox hBox = new HBox(textUsername, textSentDate);
            hBox.setSpacing(10);
            VBox vBox = new VBox(hBox, messageImage, textMessage);
            content = new HBox(imageViewUserIcon,vBox);
            content.setSpacing(10);
        }
        // When item is updated, set corresponding values to row
        @SneakyThrows
        @Override
        protected void updateItem(listViewChatRow item, boolean empty) {
            super.updateItem(item, empty);
            // Check if ListViewChatRow is empty
            if (item != null && !empty) {
                // Set username, user icon and timestamp
                textUsername.setText(item.user.getUsername());
                imageViewUserIcon.setImage(item.getUser().getUserIcon());
                textSentDate.setText(item.getTimestamp());

                //Check dataype property to know what type of message it is
                if(item.dataType == 0){ // Message type is only text message
                    // Set messageImage ImageView object hidden so it doesnt take space
                    messageImage.setVisible(false);
                    textMessage.setText(item.getMessage());
                }else if(item.dataType == 1){ // Message type is text with image
                    // Set messageImage to image from listViewChatRow object
                    messageImage.setImage(item.getFile().getImage(330));
                    textMessage.setText(item.getMessage());
                }else if(item.dataType == 2){// Message type is file
                    // Set messageImage to file icon.
                    Image fileIcon = new Image(String.valueOf(getClass().getClassLoader().getResource("fileIcon.png")),
                            50,0,true,true);
                    messageImage.setImage(fileIcon);
                    // Set text message field to file name
                    textMessage.setText(item.getFile().getFileName());

                }
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }
}

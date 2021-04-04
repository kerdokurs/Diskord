package diskord.controllers;

import diskord.client.ChatFile;
import diskord.client.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
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

    ObservableList<listViewServerRow> listViewServerData = FXCollections.observableArrayList();
    ObservableList<listViewChatRow> listViewChatData = FXCollections.observableArrayList();

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
        fxListViewServers.setCellFactory(new Callback<>() {
            @Override
            public ListCell<listViewServerRow> call(ListView<listViewServerRow> listView) {
                return new CustomServerListViewCell();
            }
        });

        // Set ObservableList of custom listViewChatRows to fxListViewChat. This way when item is added to
        // observable list, it gets added to UI
        fxListViewChat.setItems(listViewChatData);
        // Create callback so when item is added, it will handle the new item with custom cell factory.
        fxListViewChat.setCellFactory(new Callback<>() {
            @Override
            public ListCell<listViewChatRow> call(ListView<listViewChatRow> listView) {
                return new CustomChatListViewCell();
            }
        });
        // Set Server and Client Listview not selectable.
        fxListViewServers.setFocusTraversable(false);
        fxListViewChat.setFocusTraversable(false);
    }


    /**
     * JavaFX event in Main scene. Method is called when Send button is clicked.
     * Method will send message to currently open chat.
     */
    public void fxEventButtonSendChat(){
        // Check if there is any text
        if(fxTextAreaChatBox.getLength() == 0 && attachedFile == null){
            return;
        }else{
            //TODO Set max message size

            // Get user message
            String message = fxTextAreaChatBox.getText();
            String fileBase64;
            // Read user attached file to base64
            if(attachedFile != null){
                byte[] bytes = new byte[(int)attachedFile.length()];
                try(FileInputStream fis = new FileInputStream(attachedFile)){
                    fis.read(bytes);
                }catch (IOException err){
                    attachedFile = null;
                    fxTextAreaChatBox.setText(attachedFile.getName() + " not found!");
                    return;
                }
                fxLabelChatStatus.setText("");
                fileBase64 =  Base64.getEncoder().encodeToString(bytes);
            }
            //TODO send message to server. Get response code

            // Clear currently written text
            fxTextAreaChatBox.setText("");

            // Add written message to listviewChat
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime currentDate = LocalDateTime.now();
            if(attachedFile == null){ // Only message. Datatype = 0
                listViewChatData.add(
                        new listViewChatRow(
                                currentUser,            // User
                                message,                // Message
                                dtf.format(currentDate),// Timestamp
                                0,              // Datatype 0 = only message
                                null,             // Message image, currently none
                                null,               // attached file, currently none
                                null));   // attached file name, currently none
                return;
            }
            // Get file extension
            int i = attachedFile.getName().lastIndexOf('.');
            String extension = attachedFile.getName().substring(i+1).toLowerCase(Locale.ROOT);
            // Check if file is java.Image supported image type
            if(extension.equals("png") || extension.equals("gif") || extension.equals("jpeg") || extension.equals("bmp")){
                // Message with image. Datatype = 1
                try{
                    Image image = new Image(new FileInputStream(attachedFile), 200,0,true,true);
                    listViewChatData.add(
                            new listViewChatRow(
                                    currentUser,            // User
                                    message,                // Message
                                    dtf.format(currentDate),// Timestamp
                                    1,              // Datatype 1 = Message with image
                                    image,                  // Message image
                                    null,               // File.
                                    null));   // Attached file name
                }catch (IOException err){
                    attachedFile = null;
                    fxTextAreaChatBox.setText(attachedFile.getName() + " not found!");
                    return;
                }
            }else{// File. Datatype = 2

                //TODO Set file UUID that was recived from server, currently null
                ChatFile chatFile = new ChatFile(null,"test.jpg");
                listViewChatData.add(
                        new listViewChatRow(
                                currentUser,            // User
                                message,                // Message
                                dtf.format(currentDate),// Timestamp
                                2,              // Datatype
                                null,             // Image
                                chatFile,               // chatfile object
                                attachedFile.getName()));
            }
            // File sent, set attached file to null pointer
            attachedFile = null;
        }
    }

    /**
     *  JavaFX event int Main scene. Method is called when add file button is clicked.
     *  Method will add image to message
     */
    public void fxEventButtonAddFile(){
        //TODO When sending image, check that image size is not too large

        // Open file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select your file!");
        // Open file chooser in desktop folder
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Desktop"));
        attachedFile = fileChooser.showOpenDialog(null);
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
        private final String sendDate; // The date when message was sent
        @Getter
        private final int dataType;
        @Getter
        private final Image messageImage;
        @Getter
        private final ChatFile file;
        @Getter
        private final String attachedFileName;

        /**
         * Custom listview row object for chat listview.
         * @param user User object that has UUID, name and icon
         * @param message Message data
         * @param sendDate Date when message was sent
         * @param dataType Datatype that indicates what kind of functionality listviewChatRow has. 0 = only message
         *                 1 = message and image. Shows image and message in listview
         *                 2 = file. Shows image icon and file name. Also indicates that when clicked on, will
         *                 download the file from server
         * @param image    Image object that is showed over message string. Used only if datatype is 1
         * @param file     ChatFile object that has file name and file UUID. UUID is necessary so file can be
         *                 requested from server with UUID
         */
        public listViewChatRow(User user, String message, String sendDate, int dataType, Image image, ChatFile file, String attachedFileName) {
            super();
            this.user = user;
            this.message = message;
            this.sendDate = sendDate;
            this.dataType = dataType;
            this.messageImage = image;
            this.file = file;
            this.attachedFileName = attachedFileName;
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

            // Set content and its layout
            HBox hBox = new HBox(textUsername, textSentDate);
            hBox.setSpacing(10);
            VBox vBox = new VBox(hBox, messageImage, textMessage);
            // Set message text wrapping, so scroll bar doesnt appear
            textMessage.setWrappingWidth(fxListViewChat.getWidth() - 100);
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
                textSentDate.setText(item.getSendDate());

                //Check dataype property to know what type of message it is
                if(item.dataType == 0){ // Message type is only text message
                    // Set messageImage ImageView object hidden so it doesnt take space
                    messageImage.setVisible(false);
                    textMessage.setText(item.getMessage());
                }else if(item.dataType == 1){ // Message type is text with image
                    // Set messageImage to image from listViewChatRow object
                    messageImage.setImage(item.getMessageImage());
                    textMessage.setText(item.getMessage());
                }else if(item.dataType == 2){// Message type is file
                    // Set messageImage to file icon.
                    Image fileIcon = new Image(String.valueOf(getClass().getClassLoader().getResource("fileIcon.png")), 50,0,true,true);
                    messageImage.setImage(fileIcon);
                    // Set text message field to file name
                    textMessage.setText(item.getAttachedFileName());

                }
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }
}

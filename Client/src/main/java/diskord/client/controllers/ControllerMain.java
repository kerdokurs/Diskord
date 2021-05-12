package diskord.client.controllers;

import diskord.client.*;
import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.payload.PayloadType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.util.Duration;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControllerMain implements Controller{

    // FXML gui elements
    @FXML
    public ListView<listViewServerRow> fxListViewServers;
    @FXML
    public ListView<listViewChatRow> fxListViewChat;
    @FXML
    public ListView<listViewChannelRow> fxListViewChannel;
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
    public ImageView fxImageViewCurrentUserIcon;
    @FXML
    public Label fxLabelCurrentUserName;

    @FXML
    ObservableList<listViewServerRow> listViewServerData = FXCollections.observableArrayList();
    @FXML
    ObservableList<listViewChatRow> listViewChatData = FXCollections.observableArrayList();
    @FXML
    ObservableList<listViewChannelRow> listViewChannelData = FXCollections.observableArrayList();
    @FXML
    private Stage mainStage;

    // Controller objects
    File attachedFile;
    CurrentUser currentUser;
    UUID currentChatUuid;
    ServerConnection serverConnection;

    // For testing purpose. This will replace all server interaction with test data
    Boolean debugg = true;
    /**
     * Init method for ControllerMain
     */
    public void init() throws IOException {
        // Add current controller to serverConnection listener
        // TODO Server client connection
        //serverConnection.addListener(this);

        //Set current users icon and name to UI
        if(debugg){
            //TODO replace test data
            fxLabelCurrentUserName.setText(TestData.currentUser().getUsername());
            fxImageViewCurrentUserIcon.setImage(TestData.currentUser().getUserImage());
        }else{
            fxLabelCurrentUserName.setText(currentUser.getUsername());
            fxImageViewCurrentUserIcon.setImage(currentUser.getUserImage());
        }

        // Get users subscribed servers
        setUserSuscribedServers();

        // Set ObservableList of custom listView Server/Channel/Chat Rows to fxListView Server/Channel/Chat.
        // This way when item is added to observable list, it gets added to UI
        fxListViewServers.setItems(listViewServerData);
        fxListViewChat.setItems(listViewChatData);
        fxListViewChannel.setItems(listViewChannelData);
        // Create callback so when item is added, it will handle the new item with custom cell factory.
        fxListViewServers.setCellFactory(listView -> new CustomServerListViewCell());
        fxListViewChat.setCellFactory(listView -> new CustomChatListViewCell());
        fxListViewChannel.setCellFactory(listView -> new CustomChannelListViewCell());

        // Set Server,Channel and Client Listview not selectable.
        fxListViewServers.setFocusTraversable(false);
        fxListViewChat.setFocusTraversable(false);
        fxListViewChannel.setFocusTraversable(false);

        // Set Listview event handlers
        fxListViewServers.setOnMouseClicked(event -> fxEventListViewServerOnMouseClicked());
        fxListViewChannel.setOnMouseClicked(event -> fxEventListViewChannelOnMouseClicked());

        // Set fxTextAreaChatBox max character limit
        fxTextAreaChatBox.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 100 ? change : null));

        // Set fxTextAreaChatBox text to wrap
        fxTextAreaChatBox.setWrapText(true);
    }

    ////////////////////////////////////////////////////////////////////////////////// Methods

    /**
     * Method that handles servers response that is called from ServerConnection method
     * @param response ServersResponse
     * @throws IOException
     */
    public void handleResponse(Payload response){
        PayloadBody responseBody = response.getBody();
        switch (response.getType()){
            case INFO_SERVERS:
                //List<Server> servers = (List<Server>) responseBody.get("servers");
                //for (Server server:servers) {
                //    listViewServerData.add(
                //            new listViewServerRow(
                //                    server.getId(), // UUID
                //                    server.getName(), // Name
                //                    server.getDescription(), // Description
                //                    server.getServerIconFromBase64())
                //    );
                //}
                break;

        }
    }

    /**
     * Method that returns set of accepted payloadTypes in controller
     * @return Set of accepted payloadTypes in controller
     */
    @Override
    public Set<PayloadType> getListenTypes() {
        return Stream.of(PayloadType.INFO_SERVERS,PayloadType.MSG)
                .collect(Collectors.toSet());
    }

    /**
     * Method that gets all servers that user is subscribed to
     */
    public void setUserSuscribedServers() throws IOException {
        // Craft payload to get all servers
        if(debugg){
            //TODO replace test data
            List<Server> servers = TestData.userSuscribedServers();
            for (Server server:servers) {
                listViewServerData.add(
                        new listViewServerRow(
                                server.getId(), // UUID
                                server.getName(), // Name
                                server.getDescription(), // Description
                                server.getServerIconFromBase64(),
                                server.getChannels()));
            }

        }else{
            Payload serversRequest = new Payload();
            serversRequest.setType(PayloadType.INFO_SERVERS);
            serversRequest.putBody("token", currentUser.getUserToken());
            serverConnection.write(serversRequest);
        }

    }

    /**
     * Method that gets all user icons in server. First server sends all user UUID and their icons MD5 hash.
     * After that client compares the list to its locally saved list. If user icon doesnt exist or MD5 hash
     * is different, it will request new icon from server and save them to cache
     * @param serverUUID The Servers UUID where Icons are requested
     */
    public void getUsersIconsFromServer(UUID serverUUID){
        //TODO Better method naming

        //TODO Get user UUID and profile icon MD5
        // If icon is missing, request it from server

        // Retrive Chats Users with their icons MD5 hash
        List<String[]> userUuidAndMd5TestData = new ArrayList<>();

        //TODO Replace test data

        // All incoming userIcons can be saved in ChatFile.
        userUuidAndMd5TestData.add(new String[]{"bf867bfc-9541-11eb-a8b3-0242ac130003","7815696ecbf1c96e6894b779456d330e"});

        List<String> missingUserIconsUuid = new ArrayList<>();
        File diskordDir = new File(System.getenv("APPDATA"),"Diskord");
        for (int i = 0; i < userUuidAndMd5TestData.size(); i++){

            // Check if user icon exists by UUID
            File icon = new File(diskordDir, "userIcons/" + userUuidAndMd5TestData.get(i)[0] + ".png");
            // Check if users icons exist. If they do not or files md5 is wrong then request icon from server
            if(icon.exists()){
                //Check MD5 hash
                try{
                    // Get MD5 hash of file
                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    md5.update(Files.readAllBytes(icon.toPath()));
                    String signature = String.format("%032x",new BigInteger(1,md5.digest()));
                    // Compare hashes
                    if(!signature.equals(userUuidAndMd5TestData.get(i)[1])){
                        missingUserIconsUuid.add(userUuidAndMd5TestData.get(i)[0]);
                    }
                }catch (NoSuchAlgorithmException err){
                    // THIS CANNOT HAPPEN
                }catch (IOException err){
                    // Retrieve icon from server just in case
                    missingUserIconsUuid.add(userUuidAndMd5TestData.get(i)[0]);
                }
            }
        }

        //TODO Get user icons by UUID, save as chat files using missingUserIconsUUID list

        //TODO Replace test data
        List<ChatFile> userIcons = new ArrayList<>();
        ChatFile chatFile1Test = new ChatFile(UUID.randomUUID(),"bf867bfc-9541-11eb-a8b3-0242ac130003.png",
                "iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAJySURBVFhH7Zi9dcIwEMdtSEFJSQlkAVYIEzACJSVTwAahw2QJYKLQhQ6agKIzPnI6naxzgAfJi977WbJ1H39LwpZJzcGY5BYlLeoLS62oH7b8C7y0PLzA4I/keOEiv9adB+O8LRbJc7eT1GupB1zv919yG01Zr9dJx/qk1hfqhdIvLzCCnNnrDEY1SqPRMIejdREwBavlyjSbTc+33W7nedAuhC/QXmy1Wl7AECzeGTiExFFAaDbP/AAFXgaoaIDdfs9NnH7eh2jEITATYhBLVCDrVvUDfBZ6vV5eDwYDs/3YmuFw6PSLQSxiBuo4zzLPhPbzPoTajEaj/OLmfeMYURt6nSJmgCGnzrBOUOhytXL6mOsZaiOtMVgC1Ib3I2KG6WTqOJch+QPSTVKhfAlQX0pwCDQiceokqtxkWZzwHBWEEpUFRbQiJV8kKvAalAmN3WgKB2v4sOV/u3Vp+QX7wZutwVhY3Ybzb04x3YCGqVmbbrXNqVToMycGvD/hlQVuWuCVl83n1t8GcJBzcNQCq+zvOLJIOQ9HJTAkLvQWkN4cvkjfTyIqUBKnew9PHB/AFSn7caIC+bZII+6EPVi4UBB5VYE0uF6cC59yuMZMPLBRSWDZ11cMGgfOWbcHNqICYUow8Gl6ZLsypO09M/HARlSg9IuMfctypO09M/HARlQgMB6PnQQUEBv6h0B6sOM6ZqYe2FAJ3O/23nfsT8GYJLwINlQCKdrvDIl89PhfLZ8MvF7kqyyQoh1Z5/FExQERgTfbD5rAdi8tsmHW9AiHUzsvcA7l6VTdbz+IoviNsPO7jeC5QPqaJZdR/xZoDvaQJF9nEvS0a9iLwwAAAABJRU5ErkJggg==" );
        userIcons.add(chatFile1Test);

        // Save files
        for (ChatFile chatFile: userIcons){
                // Check if user icon exists by UUID
                File icon = new File(diskordDir, "userIcons/" + chatFile.getFileName());
                byte[] img = Base64.getDecoder().decode(chatFile.getBase64File());
                try (FileOutputStream stream = new FileOutputStream(icon)) {
                    stream.write(img);
                }catch (IOException err){
                    // Thrown when file cannot be accessed when it was created.
                    // It should not happen but if happens, user will have default icon
                }
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
     * Method to set currentUser to controller.
     * @param currentUser
     */
    public void setCurrentUser(CurrentUser currentUser){
        this.currentUser = currentUser;
    }

    /**
     * Method to pass servers connection to controller.
     * @param serverConnection
     */
    public void setServerConnection(ServerConnection serverConnection){
        this.serverConnection = serverConnection;
    }

    /**
     * Method adds chat info to fxListviewChat
     * @param user user object from where the message came
     * @param message The message that is displayed
     * @param timeStamp When message was sent
     * @param dataType The datatype of message
     * @param file  ChatFileObject if datatype is file
     */
    public void handleChatMessage(User  user, String message, String timeStamp, int dataType, ChatFile file){
            listViewChatData.add(
                    new listViewChatRow(
                            user,
                            message,
                            timeStamp,
                            dataType,
                            file));
    }

    ////////////////////////////////////////////////////////////////////////////////// Events

    /**
     * JavaFX event in Main scene. Method is called when Send button is clicked.
     * Method will send message to currently open chat.
     */
    public void fxEventButtonSendChat() throws IOException {
        // Check if there is any text
        if(fxTextAreaChatBox.getLength() == 0 && attachedFile == null){
            return;
        }
        //TODO Strip not working
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
        attachedFile = fileChooser.showOpenDialog(mainStage);
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

    /**
     * JavaFX event in Main scene. Method is called when fxListViewServers is clicked on
     * Method changes fxListViewChannels to clicked server channels
     */
    public void fxEventListViewServerOnMouseClicked() {
        listViewServerRow clickedServer = fxListViewServers.getSelectionModel().getSelectedItem();
        if(clickedServer != null){
            // Clear current items in listview
            fxListViewChannel.getItems().clear();
            // Add new listview Channels
            for (Channel channel: clickedServer.channels) {
                listViewChannelData.add(
                        new listViewChannelRow(
                                channel.getName(),
                                channel.getChannelIconFromBase64(),
                                channel.getUuid()
                ));
            }
        }
    }

    /**
     * JavaFX event in Main scene. Method is called when fxListViewServers is clicked on
     * Method changes fxListViewChannels to clicked server channels
     */
    public void fxEventListViewChannelOnMouseClicked() {
        listViewChannelRow clickedChannel = fxListViewChannel.getSelectionModel().getSelectedItem();
        if(clickedChannel != null){
            currentChatUuid = clickedChannel.getUuid();
            //TODO Get chats from channel with UUID
        }
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
        imageStage.initOwner(mainStage);
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
        HBox hBox = new HBox(buttonSaveImage,region1, labelImageName,region2, buttonCloseStage);
        hBox.setSpacing(5);
        VBox vBox = new VBox(hBox,imageView);
        vBox.setSpacing(5);
        pane.getChildren().add(vBox);
        imageStage.setScene(new Scene(pane));
        imageStage.show();
    }

    ////////////////////////////////////////////////////////////////////////////////// List view custom cell factory

    /**
     * Custom listViewRow class data class. This class holds servers name, description and image.
     */
    private static class listViewServerRow {
        @Getter
        private UUID uuid;
        @Getter
        private Image image;
        @Getter
        private String name;
        @Getter
        private String description;
        @Getter
        private List<Channel> channels;
        public listViewServerRow(UUID uuid, String name, String description,Image image, List<Channel> channels) {
            super();
            this.uuid = uuid;
            this.name = name;
            this.description = description;
            this.image = image;
            this.channels = channels;
        }
    }

    /**
     * Custom Listview Listcell. It holds the content of single ListViewRow
     */
    private class CustomServerListViewCell extends ListCell<listViewServerRow> {
        private HBox content;
        private ImageView imageView;
        private Tooltip tooltip;

        /**
         * Contstructor for CustomListCell. Define variables and set the layout of single cell
         */
        public CustomServerListViewCell() {
            super();
            tooltip = new Tooltip();
            // Set delay when the tooltip shows
            tooltip.setShowDelay(Duration.millis(100));
            tooltip.setHideDelay(Duration.ZERO);
            imageView = new ImageView();
            content = new HBox(imageView);
            content.setSpacing(10);
        }

        @Override
        protected void updateItem(listViewServerRow item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) { // <== test for null item and empty parameter
                tooltip.setText(item.getName() + "\n" + item.getDescription());
                imageView.setImage(item.getImage());
                Tooltip.install(content, tooltip);
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * Custom listViewRow class data class. This class holds channels name, UUID and image.
     */
    private static class listViewChannelRow {
        @Getter
        private UUID uuid;
        @Getter
        private Image image;
        @Getter
        private String name;

        /**
         * Custom listview row object for channel listview.
         * @param name The name of the channel that is displayed
         * @param image The Image of the channel that is displayed
         * @param uuid The UUID of the channel.
         */
        public listViewChannelRow(String name, Image image, UUID uuid) {
            super();
            this.uuid = uuid;
            this.name = name;
            this.image = image;
        }
    }

    /**
     * Custom Listview Listcell. It holds the content of single ListViewRow
     */
    private class CustomChannelListViewCell extends ListCell<listViewChannelRow> {
        private HBox content;
        private ImageView imageView;
        private Text text;

        /**
         * Contstructor for CustomListCell. Define variables and set the layout of single cell
         */
        public CustomChannelListViewCell() {
            super();
            text = new Text();
            imageView = new ImageView();
            content = new HBox(imageView,text);
            content.setSpacing(10);
        }

        @Override
        protected void updateItem(listViewChannelRow item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) { // <== test for null item and empty parameter
                imageView.setImage(item.getImage());
                text.setText(item.getName());
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * Custom Listview Listcell for . It holds the content of single ListViewRow
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
     * Custom Listview Listcell for Chat. It holds the content of single ListViewRow
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
                imageViewUserIcon.setImage(item.getUser().getUserIconFromFile());
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

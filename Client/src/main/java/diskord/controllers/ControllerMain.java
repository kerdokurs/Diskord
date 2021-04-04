package diskord.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMain implements Initializable {

    @FXML
    public ListView<listViewServerRow> fxListViewServers;
    @FXML
    public ListView fxListViewChat;
    @FXML
    public Label fxLabelChatMessage;
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
    /**
     * Init method for ControllerMain
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set ObservableList of custom listViewServerRows to fxListViewServer. This way when item is added to
        // observable list, it gets added to UI
        fxListViewServers.setItems(listViewServerData);
        // Create callback so when item is added, it will handle the new item with custom cell factory.
        fxListViewServers.setCellFactory(new Callback<ListView<listViewServerRow>, ListCell<listViewServerRow>>() {
            @Override
            public ListCell<listViewServerRow> call(ListView<listViewServerRow> listView) {
                return new CustomServerListViewCell();
            }
        });

        // Set ObservableList of custom listViewChatRows to fxListViewChat. This way when item is added to
        // observable list, it gets added to UI
        fxListViewChat.setItems(listViewChatData);
        // Create callback so when item is added, it will handle the new item with custom cell factory.
        fxListViewChat.setCellFactory(new Callback<ListView<listViewChatRow>, ListCell<listViewChatRow>>() {
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
     * Method will attempt to send message to currently open chat.
     */
    public void fxEventButtonSendChat() {
        // Check if there is any text
        if(fxTextAreaChatBox.getLength() == 0){
            return;
        }else{
            var message = fxTextAreaChatBox.getText();
            // SEND THE MESSAGE IN HERE. GET SERVER RESPONSE IF IT GOT DELIVERED OR NOT!
            // 0 = okay, 1 failed
            var serverResponse = 0;

        }
    }

    public void fxEventButtonAddFile() throws FileNotFoundException {
        Image img = new Image(new FileInputStream("C:\\Users\\user\\Desktop\\icon.bmp"), 40,40,false,true);
        listViewServerData.add( new listViewServerRow("aaaa", "Vaga lahe server yeehaw", img, 1234));
    }

    public void fxEventTextAreaChatOnKeyTyped() throws FileNotFoundException {
        Image img = new Image(new FileInputStream("C:\\Users\\user\\Desktop\\icon.bmp"), 40,40,false,true);
        listViewChatData.add( new listViewChatRow("Username", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", img, "3/21/2021 5:50"));
    }

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
     * Custom listViewRow Chat data class. This class holds servers name, description and image.
     */
    private static class listViewChatRow {
        private Image image; // Chat icon
        private String username;
        private String message;
        private String sendDate; // The date when message was sent

        public  String getSendDate(){
            return sendDate;
        }
        public String getUsername() {
            return username;
        }
        public String getMessage() {
            return message;
        }
        public Image getImage(){
            return image;
        }
        public listViewChatRow(String username, String message, Image image, String sendDate) {
            super();
            this.sendDate = sendDate;
            this.username = username;
            this.message = message;
            this.image = image;
        }
    }

    /**
     * Custom Listview Listcell. It holds the content of single ListViewRow
     */
    private class CustomChatListViewCell extends ListCell<listViewChatRow> {
        private HBox content;
        private ImageView imageView; // Profile icon
        private Text Username; // Username
        private TextFlow message;
        private Text sentDate;

        /**
         * Contstructor for CustomListCell. Define variables and set the layout of single cell
         */
        public CustomChatListViewCell() {
            super();
            Username = new Text();
            message = new TextFlow();
            sentDate = new Text();
            imageView = new ImageView();
            message.wid

            HBox hBox = new HBox(Username, sentDate);
            hBox.setSpacing(10);
            VBox vBox = new VBox(hBox, message);
            content = new HBox(imageView,hBox);
            content.setSpacing(10);
        }

        @Override
        protected void updateItem(listViewChatRow item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) { // <== test for null item and empty parameter
                Username.setText(item.getUsername());
                Text text = new Text();
                text.setText(item.message);
                message.getChildren().add(text);
                imageView.setImage(item.getImage());
                sentDate.setText(item.getSendDate());
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }
}

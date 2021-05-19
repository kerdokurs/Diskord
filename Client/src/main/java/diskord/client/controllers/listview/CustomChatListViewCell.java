package diskord.client.controllers.listview;

import diskord.client.ChatFileType;
import diskord.client.controllers.ControllerMain;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.SneakyThrows;

/**
 * Custom Listview Listcell for Chat. It holds the content of single ListViewRow
 */
public class CustomChatListViewCell extends ListCell<ListViewChatRow> {
    private final HBox content;
    private final ImageView imageViewUserIcon; // Profile icon
    private final Label textUsername;           // Username
    private final Text textMessage;            // Text
    private final Label textSentDate;           // Sent text
    private final ImageView messageImage;      // Image in message, if there is

    /**
     * Contstructor for CustomListCell. Define variables and set the layout of single cell
     */
    public CustomChatListViewCell(ControllerMain controllerMain) {
        super();
        // Create instances
        textUsername = new Label();
        textMessage = new Text();
        textSentDate = new Label();
        imageViewUserIcon = new ImageView();
        messageImage = new ImageView();
        // Add event listener so when image is clicked, new scene opens with the image
        messageImage.setOnMouseClicked(event -> controllerMain.fxEventListViewChatMessageImageOnMouseClicked());

        // Set message text wrapping, so scroll bar doesnt appear
        textMessage.wrappingWidthProperty().bind(controllerMain.fxListViewChat.widthProperty().subtract(70));
        textMessage.setFill(Color.rgb(255, 255, 255));
        // Set content and its layout
        HBox hBox = new HBox(textUsername, textSentDate);
        hBox.setSpacing(10);
        VBox vBox = new VBox(hBox, messageImage, textMessage);
        content = new HBox(imageViewUserIcon, vBox);
        content.setSpacing(10);
    }

    // When item is updated, set corresponding values to row
    @SneakyThrows
    @Override
    protected void updateItem(ListViewChatRow item, boolean empty) {
        super.updateItem(item, empty);
        // Check if ListViewChatRow is empty
        if (item != null && !empty) {
            // Set username, user icon and timestamp
            textUsername.setText(item.getUser().getUsername());
            imageViewUserIcon.setImage(item.getUser().getUserImage());
            textSentDate.setText(item.getTimestamp());

            //Check datatype property to know what type of message it is
            if (item.getFile() == null) { // Message type is only text message
                // Set messageImage ImageView object hidden so it doesnt take space
                messageImage.setVisible(false);
                textMessage.setText(item.getMessage());
            } else if (item.getFile().getFileType() == ChatFileType.IMAGE) { // Message type is text with image
                // Set messageImage to image from listViewChatRow object
                messageImage.setImage(item.getFile().getImage(330));
                textMessage.setText(item.getMessage());
            } else if (item.getFile().getFileType() == ChatFileType.FILE) {// Message type is file
                // Set messageImage to file icon.
                Image fileIcon = new Image(String.valueOf(getClass().getClassLoader().getResource("fileIcon.png")),
                        50, 0, true, true);
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
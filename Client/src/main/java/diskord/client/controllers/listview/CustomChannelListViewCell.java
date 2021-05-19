package diskord.client.controllers.listview;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Custom Listview Listcell. It holds the content of single ListViewRow
 */
public class CustomChannelListViewCell extends ListCell<ListViewChannelRow> {
    private HBox content;
    private ImageView imageView;
    private Label text;

    /**
     * Contstructor for CustomListCell. Define variables and set the layout of single cell
     */
    public CustomChannelListViewCell() {
        super();
        text = new Label();
        imageView = new ImageView();
        content = new HBox(imageView, text);
        content.setSpacing(10);
    }

    @Override
    protected void updateItem(ListViewChannelRow item, boolean empty) {
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
package diskord.client.controllers.listview;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Custom Listview Listcell. It holds the content of single ListViewRow
 */
public class CustomUsersListViewCell extends ListCell<ListViewUsersRow> {
    private final HBox content;
    private final ImageView imageView;
    private final Label label;


    /**
     * Contstructor for CustomListCell. Define variables and set the layout of single cell
     */
    public CustomUsersListViewCell() {
        super();
        label = new Label();
        imageView = new ImageView();
        content = new HBox(imageView, label);
        content.setSpacing(10);
    }

    @Override
    protected void updateItem(ListViewUsersRow item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) { // <== test for null item and empty parameter
            label.setText(item.getName());
            imageView.setImage(item.getImage());
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}
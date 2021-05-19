package diskord.client.controllers.listview;

import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * Custom Listview Listcell. It holds the content of single ListViewRow
 */
public class CustomServerListViewCell extends ListCell<ListViewServerRow> {
    private final HBox content;
    private final ImageView imageView;
    private final Tooltip tooltip;

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
    protected void updateItem(ListViewServerRow item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) { // <== test for null item and empty parameter
            tooltip.setText(
                    item.getName() + "\n" +
                            item.getDescription() + "\n" +
                            "Join ID: " + item.getJoinID());
            imageView.setImage(item.getImage());
            Tooltip.install(content, tooltip);
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}
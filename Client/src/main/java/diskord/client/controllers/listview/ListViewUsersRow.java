package diskord.client.controllers.listview;

import javafx.scene.image.Image;
import lombok.Getter;

/**
 * Custom listViewRow class data class.
 */
public class ListViewUsersRow {
    @Getter
    private final Image image;
    @Getter
    private final String name;

    public ListViewUsersRow(String name, Image image) {
        super();
        this.name = name;
        this.image = image;
    }

    @Override
    public String toString() {
        return "ListViewUsersRow{" +
                "image=" + image +
                ", name='" + name + '\'' +
                '}';
    }
}
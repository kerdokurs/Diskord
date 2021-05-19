package diskord.client.controllers.listview;

import javafx.scene.image.Image;
import lombok.Getter;

import java.util.UUID;

/**
 * Custom listViewRow class data class. This class holds servers name, description and image.
 */
public class ListViewServerRow {

    @Getter
    private final UUID uuid;
    @Getter
    private final Image image;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String joinID;

    public ListViewServerRow(UUID uuid, String name, String description, Image image, String joinID) {

        super();
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.image = image;

        this.joinID = joinID;
    }

    @Override
    public String toString() {
        return "ListViewServerRow{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", joinID='" + joinID + '\'' +

                '}';
    }
}
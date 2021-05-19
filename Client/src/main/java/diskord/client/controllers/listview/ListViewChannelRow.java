package diskord.client.controllers.listview;

import javafx.scene.image.Image;
import lombok.Getter;

import java.util.UUID;

/**
 * Custom listViewRow class data class. This class holds channels name, UUID and image.
 */
public class ListViewChannelRow {
    @Getter
    private final String name;
    @Getter
    private final UUID uuid;
    @Getter
    private final Image image;
    /**
     * Custom listview row object for channel listview.
     *
     * @param name  The name of the channel that is displayed
     * @param image The Image of the channel that is displayed
     * @param uuid  The UUID of the channel.
     */
    public ListViewChannelRow(String name, Image image, UUID uuid) {
        super();
        this.uuid = uuid;
        this.name = name;
        this.image = image;
    }
    @Override
    public String toString() {
        return "listViewChannelRow{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
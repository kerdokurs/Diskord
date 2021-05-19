package diskord.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

public class CurrentUser extends  User{
    @Getter
    @Setter
    private ObservableList<UUID> privilegedServers;
    @Getter
    private final String userToken;
    public CurrentUser(String username, UUID userUUID, String userToken) {
        super(username, userUUID);
        this.userToken = userToken;
        this.privilegedServers =  FXCollections.observableArrayList();
    }

    public CurrentUser(String username, UUID userUUID, String userToken, String imageBase64) {
        super(username, userUUID, imageBase64);
        this.userToken = userToken;
        this.privilegedServers =  FXCollections.observableArrayList();
    }

    public CurrentUser(String username, UUID user, String userToken, Image image) {
        super(username, user, image);
        this.userToken = userToken;
        this.privilegedServers =  FXCollections.observableArrayList();
    }
}

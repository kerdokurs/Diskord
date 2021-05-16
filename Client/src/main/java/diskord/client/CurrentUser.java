package diskord.client;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public class CurrentUser extends  User{
    @Getter
    @Setter
    private List<UUID> privilegedServers;
    @Getter
    private final String userToken;
    public CurrentUser(String username, UUID userUUID, String userToken) {
        super(username, userUUID);
        this.userToken = userToken;
    }

    public CurrentUser(String username, UUID userUUID, String userToken, String imageBase64) {
        super(username, userUUID, imageBase64);
        this.userToken = userToken;
    }

    public CurrentUser(String username, UUID user, String userToken, Image image) {
        super(username, user, image);
        this.userToken = userToken;
    }
}

package diskord.client;

import javafx.scene.image.Image;
import lombok.Getter;

import java.util.UUID;

public class CurrentUser extends  User{
    @Getter
    private String userRole;
    @Getter
    private String userToken;
    public CurrentUser(String username, UUID userUUID, String userToken, String userRole) {
        super(username, userUUID);
        this.userToken = userToken;
        this.userRole = userRole;
    }

    public CurrentUser(String username, UUID userUUID, String userToken, String userRole, String imageBase64) {
        super(username, userUUID, imageBase64);
        this.userToken = userToken;
        this.userRole = userRole;
    }

    public CurrentUser(String username, UUID user, String userToken, String userRole, Image image) {
        super(username, user, image);
        this.userToken = userToken;
        this.userRole = userRole;
    }
}

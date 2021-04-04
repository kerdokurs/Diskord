package diskord.client;

import javafx.scene.image.Image;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

public class User {
    @Getter
    private String username;
    @Getter
    private UUID userUUID;

    public User(String username, UUID user) {
        this.username = username;
        this.userUUID = user;
    }

    public Image getUserIcon() throws FileNotFoundException {
        //TODO trycatch filenotfoundexecption
        File diskordDir = new File(System.getenv("APPDATA"),"Diskord");
        // Check if user icon exists by UUID
        File icon = new File(diskordDir, "userIcons/" + userUUID.toString() + ".png");
        if(icon.exists()){
            return new Image(new FileInputStream(icon), 40,40,false,true);
        }else{
            return new Image(new FileInputStream(new File(diskordDir,"userIcons/null.png")), 40,40,false,true);
        }
    }
}

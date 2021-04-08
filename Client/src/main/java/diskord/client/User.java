package diskord.client;

import javafx.scene.image.Image;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

public class User {
    @Getter
    private final String username;
    @Getter
    private final UUID userUUID;
    @Getter
    private final Image userImage;
    public User(String username, UUID user) {
        this.username = username;
        this.userUUID = user;
        this.userImage = getUserImage();
    }

    public Image getUserIcon(){
        File diskordDir = new File(System.getenv("APPDATA"),"Diskord");
        // Check if user icon exists by UUID
        File icon = new File(diskordDir, "userIcons/" + userUUID.toString() + ".png");
        try{
            if(icon.exists()){
                return new Image(new FileInputStream(icon), 40,40,false,true);
            }else{
                return new Image(String.valueOf(getClass().getClassLoader().getResource("emptyProfileIcon.png")), 50,0,true,true);
            }
        }catch (FileNotFoundException err){
            return new Image(String.valueOf(getClass().getClassLoader().getResource("emptyProfileIcon.png")), 50,0,true,true);
        }
    }
}

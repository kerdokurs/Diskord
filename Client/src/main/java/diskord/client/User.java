package diskord.client;

import javafx.scene.image.Image;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Base64;
import java.util.UUID;

public class User {
    @Getter
    private final String username;
    @Getter
    private final UUID userUUID;
    @Getter
    private final Image userImage;

    private final Logger logger = LogManager.getLogger(getClass().getName());

    /**
     * User constructor that takes in username and user UUID
     * User icon is retrieved from appdata/diskord folder by UUID
     * @param username
     * @param userUUID
     */
    public User(String username, UUID userUUID) {
        this.username = username;
        this.userUUID = userUUID;
        this.userImage = getUserIconFromFile();
    }

    /**
     * User constructor that takes in username, user UUID and base 64 string of icon
     * @param username
     * @param userUUID
     * @param imageBase64
     */
    public User(String username, UUID userUUID, String imageBase64){
        Image userImage_;
        this.username = username;
        this.userUUID = userUUID;
        try{
            byte[] img = Base64.getDecoder().decode(imageBase64);
            InputStream stream = new ByteArrayInputStream(img);
            userImage_ = new Image(stream, 40,40,true,true);
        }catch (NullPointerException | IllegalArgumentException err){
            userImage_ = Utils.generateImage(40,40,1,1,1,1);
            logger.error(err.getMessage());
        }
        this.userImage = userImage_;
    }

    /**
     * User constructor that takes in username, user UUID and user icon to use
     * @param username
     * @param userUUID
     * @param image
     */
    public User(String username, UUID userUUID,Image image) {
        this.username = username;
        this.userUUID = userUUID;
        this.userImage = image;
    }

    public Image getUserIconFromFile(){
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
            logger.error(err.getMessage());
            return new Image(String.valueOf(getClass().getClassLoader().getResource("emptyProfileIcon.png")), 50,0,true,true);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userUUID=" + userUUID +
                '}';
    }
}

package diskord.client;

import javafx.scene.image.Image;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

public class Channel {
    @Getter
    private final String name;
    @Getter
    private final UUID uuid;
    @Getter
    private final String base64Icon;
    @Getter
    private final UUID serverUUID;

    public Channel(String name, UUID uuid, String base64Icon,UUID serverUUID) {
        this.name = name;
        this.uuid = uuid;
        this.base64Icon = base64Icon;
        this.serverUUID = serverUUID;
    }

    public Image getChannelIconFromBase64(){
        try{
            byte[] img = Base64.getDecoder().decode(base64Icon);
            InputStream stream = new ByteArrayInputStream(img);
            return new Image(stream, 40,40,false,true);
        }catch (IllegalArgumentException err){
            // Base64 is not valid. Create blank image
            return  Utils.generateImage(40,40,1,1,1,1);
        }
    }
}

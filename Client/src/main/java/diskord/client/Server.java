package diskord.client;

import javafx.scene.image.Image;
import lombok.Getter;
import java.io.*;
import java.util.*;

public class Server {
    @Getter
    private final UUID id;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String base64Icon;
    @Getter
    private final String joinID;

    public Server(UUID id, String name, String description, String base64Icon, String joinID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.base64Icon = base64Icon;
        this.joinID = joinID;
    }

    /**
     * Creates Image from base64
     * @return Image for server
     */
    public Image getServerIconFromBase64(){
        try{
            byte[] img = Base64.getDecoder().decode(base64Icon);
            InputStream stream = new ByteArrayInputStream(img);
            return new Image(stream, 40,40,false,true);
        }catch (IllegalArgumentException | NullPointerException err){
            // Base64 is not valid. Create blank image
            return  Utils.generateImage(40,40,1,1,1,1);
        }
    }
}

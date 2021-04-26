package diskord.client;

import javafx.scene.image.Image;
import lombok.Getter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

public class ChatFile {
    @Getter
    private final UUID fileUUID;
    @Getter
    private final String fileName;
    @Getter
    private final String base64File;

    public ChatFile(UUID fileUUID, String fileName, String base64File) {
        this.fileUUID = fileUUID;
        this.fileName = fileName;
        this.base64File = base64File;
    }

    /**
     * Method decodes base64 image to image. Only use this method when you know that base64File is image
     * @param requestedWidth Requested width. No height so image preserver ratio. If requested width
     *                       is 0, keep images original width.
     * @return Returns Image that is created with requested width
     */
    public Image getImage(int requestedWidth){
        byte[] img = Base64.getDecoder().decode(base64File);
        InputStream stream = new ByteArrayInputStream(img);
        if(requestedWidth == 0){
            return new Image(stream);
        }else{
            return new Image(stream, requestedWidth,0,true,true);
        }

    }
}

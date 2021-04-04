package diskord.client;

import lombok.Getter;

import java.util.UUID;

public class ChatFile {
    @Getter
    private final UUID fileUUID;
    @Getter
    private final String fileName;

    public ChatFile(UUID fileUUID, String fileName) {
        this.fileUUID = fileUUID;
        this.fileName = fileName;
    }
}

package diskord.payload.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class ChatFileDTO {
    @Getter
    @Setter
    private UUID fileUUID;
    @Getter
    @Setter
    private String fileName;
    @Getter
    @Setter
    private String base64File;
    @Getter
    @Setter
    private String fileType;

    public String toJson(final ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

    public static ChatFileDTO fromJson(final ObjectMapper mapper, final String content) throws JsonProcessingException {
        return mapper.readValue(content, ChatFileDTO.class);
    }
}

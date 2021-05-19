package diskord.payload.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class UserDTO {
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private UUID userId;
    @Getter
    @Setter
    private String base64Icon;

    public String toJson(final ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

    public static UserDTO fromJson(final ObjectMapper mapper, final String content) throws JsonProcessingException {
        return mapper.readValue(content, UserDTO.class);
    }
}
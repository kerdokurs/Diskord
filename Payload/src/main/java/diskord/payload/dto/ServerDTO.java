package diskord.payload.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class ServerDTO {
  @Getter
  @Setter
  private UUID id;

  @Getter
  @Setter
  private String name;

  @Getter
  @Setter
  private String description;

  @Getter
  @Setter
  private String base64Icon;

  @Getter
  @Setter
  private String joinID;

  public String toJson(final ObjectMapper mapper) throws JsonProcessingException {
    return mapper.writeValueAsString(this);
  }

  public static ServerDTO fromJson(final ObjectMapper mapper, final String content) throws JsonProcessingException {
    return mapper.readValue(content, ServerDTO.class);
  }
}

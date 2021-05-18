package diskord.payload.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class ChannelDTO {
  @Getter
  @Setter
  private String name;

  @Getter
  @Setter
  private UUID uuid;

  @Getter
  @Setter
  private String base64Icon;

  public String toJson(final ObjectMapper mapper) throws JsonProcessingException {
    return mapper.writeValueAsString(this);
  }
}

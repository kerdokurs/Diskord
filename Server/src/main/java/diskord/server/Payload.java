package diskord.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

enum PayloadType {
  BINK, // ping, aga meie ikooniga sobivas kirjastiili
}

public class Payload implements Serializable {
  // Hoiustame mapperi, et seda mitte iga kord, kui vaja deserialiseerida, luua.
  public static final ObjectMapper mapper = new ObjectMapper();

  @Getter
  @Setter
  private PayloadType type;

  public static Payload fromJson(final String json) throws JsonProcessingException {
    return mapper.readValue(json, Payload.class);
  }
}
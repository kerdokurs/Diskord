package diskord.payload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Payload implements Serializable {
  @Getter
  @Setter
  private PayloadType type;

  // Payloadi id (võiks olla, võib-olla parem hiljem hallata)
  @Getter
  @Setter
  private UUID id;

  @Getter
  @Setter
  private UUID responseTo;

  @Getter
  @Setter
  private Date timestamp;

  // Allkirjastatud jsonwebtoken, mille abil saame valideerida kasutaja ja tema õigused.
  @Getter
  @Setter
  private String jwt;

  @Getter
  @Setter
  private PayloadBody body;

  public Payload() {
    timestamp = new Date();
    body = new PayloadBody();
    id = UUID.randomUUID();
  }

  public static Payload fromJson(final ObjectMapper mapper, final String json) throws JsonProcessingException {
    return mapper.readValue(json, Payload.class);
  }

  public String toJson(final ObjectMapper mapper) throws JsonProcessingException {
    return mapper.writeValueAsString(this);
  }

  public Payload putBody(final String key, final Object value) {
    getBody().put(key, value);
    return this;
  }

  @Override
  public String toString() {
    return "diskord.payload.Payload{" +
        "type=" + type +
        ", id=" + id +
        ", responseTo=" + responseTo +
        ", timestamp=" + timestamp +
        ", jwt='" + jwt + '\'' +
        ", body=" + body +
        '}';
  }
}

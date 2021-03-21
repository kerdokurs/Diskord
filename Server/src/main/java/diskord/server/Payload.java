package diskord.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

// Tegemist on payloadi tüübiga, mis määrab ära, mida tegema peab
enum PayloadType {
  BINK, // ping, aga meie ikooniga sobivas kirjastiilis.
  CHAT,
  JOIN,
  LEAVE,
}

public class Payload implements Serializable {
  // Hoiustame mapperi, et seda mitte iga kord, kui vaja deserialiseerida, luua.
  public static final ObjectMapper mapper = new ObjectMapper();

  @Getter
  @Setter
  private PayloadType type;

  // Payloadi id (võiks olla, võib-olla parem hiljem hallata)
  @Getter
  @Setter
  private UUID id;

  // Allkirjastatud jsonwebtoken, mille abil saame valideerida kasutaja ja tema õigused.
  @Getter
  @Setter
  private String jwt;

  public static Payload fromJson(final String json) throws JsonProcessingException {
    return mapper.readValue(json, Payload.class);
  }
}
package diskord.server.dto;

import diskord.payload.dto.ServerDTO;
import diskord.server.database.room.Room;
import org.modelmapper.ModelMapper;

public class ConvertServer {
  public static ServerDTO convert(final ModelMapper mapper, final Room room) {
    return mapper.map(room, ServerDTO.class)
      .setBase64Icon(room.getIcon());
  }
}

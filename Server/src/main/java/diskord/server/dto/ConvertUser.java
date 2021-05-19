package diskord.server.dto;

import diskord.payload.dto.UserDTO;
import diskord.server.ConnectedClient;
import diskord.server.database.DatabaseManager;
import diskord.server.database.user.User;
import org.modelmapper.ModelMapper;

import java.util.UUID;

public class ConvertUser {
  public static UserDTO convert(final ModelMapper mapper, final ConnectedClient client, final DatabaseManager dbManager) {
    final UUID userId = client.getUserId();
    final User user = dbManager.getOne(User.class, userId); // TODO: Fix by putting users in the connection map

    if (user == null) return null;

    final UserDTO userDto = mapper.map(user, UserDTO.class);
    userDto.setUserId(user.getId());
    userDto.setBase64Icon(user.getIcon());

    return userDto;
  }
}

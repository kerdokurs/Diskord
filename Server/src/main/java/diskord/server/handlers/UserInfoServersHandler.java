package diskord.server.handlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.payload.Payload;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;
import diskord.server.database.transactions.RoomTransactions;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.JoinedServer;
import diskord.server.database.user.PrivilegedServer;
import diskord.server.database.user.User;
import diskord.server.dto.ConvertServer;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.INFO_USER_SERVERS_ERROR;
import static diskord.payload.PayloadType.INFO_USER_SERVERS_OK;
import static diskord.payload.ResponseType.TO_SELF;

public class UserInfoServersHandler extends Handler {
  private final ModelMapper modelMapper = new ModelMapper();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public UserInfoServersHandler(DatabaseManager dbManager, ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  /**
   * Method which each handler must override that handles a specific type of request.
   * For example: LOGIN, REGISTER or MSG.
   *
   * @param request incoming request
   * @param channel
   * @return response to that request
   */
  @Override
  public Payload handleRequest(Payload request, Channel channel) {
    Payload response = new Payload();
    response.setResponseTo(request.getId());
    response.setResponseType(TO_SELF);

    try {
      final String jwt = request.getJwt();
      if (jwt == null) throw new IllegalStateException();

      DecodedJWT decoded = Auth.decode(jwt);
      final User user = UserTransactions.getUserByUsername(dbManager, decoded.getSubject());

      List<String> joinedServers = new ArrayList<>();
      final List<JoinedServer> joinedRooms = UserTransactions.getUserJoinedRooms(dbManager, user.getId());
      for (final JoinedServer joinedRoom : joinedRooms) {
        final Room room = RoomTransactions.getRoomByUUID(dbManager, joinedRoom.getRoomId());
        try {
          // Mapping ServerDTO to string for client to parse it itself
          final String serverStr = ConvertServer.convert(modelMapper, room).toJson(objectMapper);
          joinedServers.add(serverStr);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }

      List<String> privilegedServers = new ArrayList<>();
      final List<PrivilegedServer> privilegedRooms = UserTransactions.getUserPrivilegedRooms(dbManager, user.getId());
      for (final PrivilegedServer privilegedRoom : privilegedRooms) {
        final UUID roomId = privilegedRoom.getRoomId();
        privilegedServers.add(roomId.toString());
      }

      response
        .putBody("joined", joinedServers)
        .putBody("privileged", privilegedServers);
    } catch (JWTVerificationException | IllegalStateException err) {
      err.printStackTrace();
      return response
        .setType(INFO_USER_SERVERS_ERROR)
        .putBody(BODY_MESSAGE, "Decoding jwt token that was received from client failed.");
    }

    return response.setType(INFO_USER_SERVERS_OK);
  }
}

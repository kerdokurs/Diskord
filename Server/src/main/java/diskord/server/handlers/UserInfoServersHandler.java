package diskord.server.handlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.payload.PayloadType;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;
import diskord.server.database.transactions.RoomTransactions;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import java.util.*;

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.INFO_USER_SERVERS_ERROR;
import static diskord.payload.PayloadType.INFO_USER_SERVERS_OK;

public class UserInfoServersHandler extends Handler{
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

    try{
      DecodedJWT decoded = Auth.decode(request.getJwt());
      final User user = UserTransactions.getUserByUsername(dbManager, decoded.getSubject());

      try{
        Set<Room> joinedServers = new HashSet<>();
        try {
          for (UUID id : user.getJoinedServers()) {
            joinedServers.add(RoomTransactions.getRoomByUUID(dbManager, id));
          }
        } catch(Exception e){
          return response
            .setType(INFO_USER_SERVERS_ERROR)
            .putBody(BODY_MESSAGE, "Converting User's Set<UUID> to Set<Room> failed.");
        }

        response
          .putBody("joined", joinedServers)
          .putBody("privileged", user.getPrivilegedServers());

      } catch (Exception e){
        return response
          .setType(INFO_USER_SERVERS_ERROR)
          .putBody(BODY_MESSAGE, "Error getting user joined server Sets<>.");

      }

    } catch (JWTVerificationException err){
      response
        .setType(INFO_USER_SERVERS_ERROR)
        .putBody(BODY_MESSAGE, "Decoding jwt token that was received from client failed.");
    }
    return response.setType(INFO_USER_SERVERS_OK);
  }
}

package diskord.server.handlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;
import diskord.server.database.transactions.RoomTransactions;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import static diskord.payload.PayloadBody.*;
import static diskord.payload.PayloadType.JOIN_SERVER_ERROR;
import static diskord.payload.PayloadType.JOIN_SERVER_OK;
import static diskord.payload.ResponseType.TO_SELF;

public class JoinServerHandler extends Handler {
  public JoinServerHandler(DatabaseManager dbManager, ServerHandler serverHandler) {
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
    //<server_id, jwt token>
    try {
      DecodedJWT decoded = Auth.decode(request.getJwt());
      final User user = UserTransactions.getUserByUsername(dbManager, decoded.getSubject());

      final String joinId = (String) request.getBody().get(SERVER_ID);
      if (joinId == null) {
        return response
          .setType(JOIN_SERVER_ERROR)
          .putBody(BODY_MESSAGE, "Please provide a server id");
      }

      final Room room = RoomTransactions.getRoomByJoinId(dbManager, joinId);
      if (room == null) {
        return response
          .setType(JOIN_SERVER_ERROR)
          .putBody(BODY_MESSAGE, "Join id is not valid");
      }

      final boolean saved = UserTransactions.addUserJoinedServer(dbManager, user, room);
      if (!saved) {
        return response
          .setType(JOIN_SERVER_ERROR)
          .putBody(BODY_MESSAGE, "Error joining server");
      }
    } catch (JWTVerificationException err) {
      return response
        .setType(JOIN_SERVER_ERROR)
        .putBody(BODY_FIELD, "server")
        .putBody(BODY_MESSAGE, "Decoding the jwt token received from server failed.");
    }

    return response.setType(JOIN_SERVER_OK);
  }
}

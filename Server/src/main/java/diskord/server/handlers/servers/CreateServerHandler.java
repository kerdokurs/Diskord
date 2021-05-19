package diskord.server.handlers.servers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.handlers.Handler;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.REGISTER_SERVER_ERROR;
import static diskord.payload.PayloadType.REGISTER_SERVER_OK;
import static diskord.payload.ResponseType.TO_SELF;

public class CreateServerHandler extends Handler {
  public CreateServerHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    final Payload response = new Payload()
      .setResponseTo(request.getId())
      .setType(REGISTER_SERVER_ERROR)
      .setResponseType(TO_SELF);

    final String jwt = request.getJwt();

    final User user;

    try {
      final DecodedJWT decoded = Auth.decode(jwt);
      final String username = decoded.getSubject();
      user = UserTransactions.getUserByUsername(dbManager, username);
    } catch (final JWTVerificationException e) {
      return response
        .putBody(BODY_MESSAGE, "Error decoding login token. Try logging out and back in");
    }

    if (user == null) {
      return response
        .putBody(BODY_MESSAGE, "Error getting the user (is null).");
    }

    final String name = (String) request.getBody().get("name");
    final String description = (String) request.getBody().get("description");
    final String icon = (String) request.getBody().get("icon");

    if (name == null || description == null || icon == null) {
      return response
        .setType(REGISTER_SERVER_ERROR)
        .setResponseType(TO_SELF)
        .putBody(BODY_MESSAGE, "Invalid name, description or icon");
    }

    final Room room = new Room(name, description, icon);
    dbManager.save(room);

    final boolean savedJoinedServer = UserTransactions.addUserJoinedServer(dbManager, user, room);
    if (!savedJoinedServer) {
      return response
        .putBody(BODY_MESSAGE, "Error joining the server");
    }

    final boolean savedPrivilegedServer = UserTransactions.addUserPrivilegedServer(dbManager, user, room);
    if (!savedPrivilegedServer) {
      return response
        .putBody(BODY_MESSAGE, "Error saving privileged server");
    }

    return response
      .setType(REGISTER_SERVER_OK);
  }
}

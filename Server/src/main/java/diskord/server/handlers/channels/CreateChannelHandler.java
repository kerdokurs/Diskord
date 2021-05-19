package diskord.server.handlers.channels;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import diskord.payload.ResponseType;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;
import diskord.server.database.transactions.RoomTransactions;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.handlers.Handler;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import java.util.UUID;

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.REGISTER_CHANNEL_OK;

public class CreateChannelHandler extends Handler {
  public CreateChannelHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    final Payload response = new Payload()
      .setResponseTo(request.getId())
      .setType(PayloadType.REGISTER_CHANNEL_ERROR)
      .setResponseType(ResponseType.TO_SELF);

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
    final String icon = (String) request.getBody().get("icon");

    final String serverIdStr = (String) request.getBody().get("server_id");

    if (name == null || icon == null || serverIdStr == null) {
      return response
        .putBody(BODY_MESSAGE, "Invalid name, server or icon");
    }

    final UUID serverId = UUID.fromString(serverIdStr);
    final Room room = RoomTransactions.getRoomByUUID(dbManager, serverId);

    // TODO: It's not null. Error will rise.
    if (room == null) {
      return response
        .putBody(BODY_MESSAGE, "Room not found");
    }

    final diskord.server.database.channel.Channel createdChannel = new diskord.server.database.channel.Channel()
      .setName(name)
      .setIcon(icon)
      .setRoomId(serverId);
    dbManager.save(createdChannel); // TODO: Try-catch

    return response
      .setType(REGISTER_CHANNEL_OK);
  }
}

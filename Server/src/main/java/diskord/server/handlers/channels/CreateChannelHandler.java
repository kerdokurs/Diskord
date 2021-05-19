package diskord.server.handlers.channels;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import diskord.payload.ResponseType;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.handlers.Handler;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.SERVER_FVKD_UP;

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
    System.out.println(user);

    return response
      .setType(SERVER_FVKD_UP);
  }
}

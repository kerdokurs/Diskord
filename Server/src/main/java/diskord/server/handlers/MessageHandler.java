package diskord.server.handlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import java.util.UUID;

import static diskord.payload.ResponseType.TO_ALL_EXCEPT_SELF;
import static diskord.payload.ResponseType.TO_SELF;

public class MessageHandler extends Handler {
  protected MessageHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    final Payload response = new Payload();
    response.setResponseTo(request.getId());

    final String token = request.getJwt();
    final String message = (String) request.getBody().get("message"); // TODO: Validate

    // TODO: Get and validate optionally attached file

    try {
      final DecodedJWT decoded = Auth.decode(token);
      final String username = decoded.getSubject();

      final User user = UserTransactions.getUserByUsername(dbManager, username);

      final UUID messageId = UUID.randomUUID();

      return response
        .setResponseType(TO_ALL_EXCEPT_SELF)
        .putBody("id", messageId.toString())
        .putBody("message", message)
        .putBody("user_id", user.getId().toString())
        .putBody("username", username);
    } catch (final JWTVerificationException e) {
      return response
        .setResponseType(TO_SELF)
        .putBody("message", "Error with provided login token. Try logging out and logging in.");
    }
  }
}
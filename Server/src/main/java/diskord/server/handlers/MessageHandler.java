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

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.MSG;
import static diskord.payload.PayloadType.MSG_ERROR;
import static diskord.payload.ResponseType.TO_CHANNEL_EXCEPT_SELF;
import static diskord.payload.ResponseType.TO_SELF;

public class MessageHandler extends Handler {
  public MessageHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    final Payload response = new Payload();
    response.setResponseTo(request.getId());

    final String token = request.getJwt();
    final String message = (String) request.getBody().get(BODY_MESSAGE);
    final Object file = request.getBody().get("chat_file");

//    if (file != null && (message == null || message.isEmpty() || message.length() >= 500)) {
//      return response
//        .setResponseType(TO_SELF)
//        .setType(MSG_ERROR)
//        .putBody(BODY_MESSAGE, "Invalid message length. Message must contain 0-500 characters.");
//    }

    try {
      final DecodedJWT decoded = Auth.decode(token);
      final String username = decoded.getSubject();

      final User user = UserTransactions.getUserByUsername(dbManager, username);

      final UUID messageId = UUID.randomUUID();

      if (file != null)
        response.putBody("chat_file", file);

      return response
        .setType(MSG)
        .setResponseType(TO_CHANNEL_EXCEPT_SELF)
        .putBody("id", messageId.toString())
        .putBody("message", message)
        .putBody("user_id", user.getId().toString())
        .putBody("username", username);
    } catch (final JWTVerificationException e) {
      return response
        .setResponseType(TO_SELF)
        .putBody("message", "Error with provided login token. Try logging out and back in.");
    }
  }
}

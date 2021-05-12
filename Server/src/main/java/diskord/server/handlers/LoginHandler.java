package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.server.crypto.Auth;
import diskord.server.crypto.Hash;
import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.init.ServerHandler;
import diskord.server.utils.CredentialValidator;
import io.netty.channel.Channel;

import javax.persistence.NoResultException;
import java.util.Map;

import static diskord.payload.PayloadBody.*;
import static diskord.payload.PayloadType.LOGIN_ERROR;
import static diskord.payload.PayloadType.LOGIN_OK;
import static diskord.server.utils.credentials.CredentialConstraint.NULL_CONSTRAINT;

public class LoginHandler extends Handler {
  public LoginHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    Payload response = new Payload();
    response.setResponseTo(request.getId());

    // Getting submitted data from the payload body
    final PayloadBody body = request.getBody();
    String username = (String) body.get(BODY_USERNAME);
    String password = (String) body.get(BODY_PASSWORD);

    // Validating username and password on login
    String loginUsernameError = CredentialValidator.validate(
      username,
      NULL_CONSTRAINT
    );

    if (loginUsernameError != null) {
      return response
        .setType(LOGIN_ERROR)
        .putBody(BODY_FIELD, BODY_USERNAME)
        .putBody(BODY_MESSAGE, loginUsernameError);
    }

    String loginPasswordError = CredentialValidator.validate(
      password,
      NULL_CONSTRAINT
    );

    if (loginPasswordError != null) {
      return response
        .setType(LOGIN_ERROR)
        .putBody(BODY_FIELD, BODY_PASSWORD)
        .putBody(BODY_MESSAGE, loginPasswordError);
    }

    try {
      // Fetching user specified by its username from the database
      final User user = UserTransactions.getUserByUsername(dbManager, username);

      // Hashing provided password to compare against the one in database
      final String hashedPassword = Hash.hash(password);

      // Comparing the passwords
      if (user.getPassword().equals(hashedPassword)) {
        // Login was successful, generating jsonwebtoken
        String loginToken = Auth.encode(user.getUsername(), Map.of());

        // Populating response with proper data
        response
          .setType(LOGIN_OK)
          .putBody(BODY_TOKEN, loginToken);
      } else {
        // Password was incorrect, inform client
        response
          .setType(LOGIN_ERROR)
          .putBody(BODY_MESSAGE, "Wrong password!");
      }
    } catch (NoResultException e) {
      // User was not found, inform client
      response
        .setType(LOGIN_ERROR)
        .putBody(BODY_MESSAGE, "User does not exist.");
    }

    return response;
  }
}

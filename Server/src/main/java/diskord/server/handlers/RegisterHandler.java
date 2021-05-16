package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.user.Role;
import diskord.server.database.user.User;
import diskord.server.init.ServerHandler;
import diskord.server.utils.CredentialValidator;
import io.netty.channel.Channel;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static diskord.payload.PayloadBody.*;
import static diskord.payload.PayloadType.*;
import static diskord.server.utils.credentials.CredentialConstraint.*;
import static diskord.server.utils.credentials.CredentialConstraint.TOO_LONG_CONSTRAINT;

public class RegisterHandler extends Handler {
  public RegisterHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    Payload response = new Payload();
    response.setResponseTo(request.getId());

    // Getting register data from the request body
    final PayloadBody body = request.getBody();
    String username = (String) body.get(BODY_USERNAME);
    String password = (String) body.get(BODY_PASSWORD);

    // Validating username
    final String usernameError = CredentialValidator.validate(
      username,
      NULL_CONSTRAINT,
      TOO_SHORT_CONSTRAINT,
      TOO_LONG_CONSTRAINT
    );

    // If username is not valid, return a response with information
    if (usernameError != null) {
      return response
        .setType(REGISTER_ERROR)
        .putBody(BODY_FIELD, BODY_USERNAME)
        .putBody(BODY_MESSAGE, usernameError);
    }

    // Validating password
    final String passwordError = CredentialValidator.validate(
      password,
      NULL_CONSTRAINT,
      TOO_SHORT_CONSTRAINT,
      TOO_LONG_CONSTRAINT
    );

    // If password is not valid, return a response with information
    if (passwordError != null) {
      return response
        .setType(REGISTER_ERROR)
        .putBody(BODY_FIELD, BODY_PASSWORD)
        .putBody(BODY_MESSAGE, passwordError);
    }

    try {
      // Trying to find existing user. When it's found alert user that the username
      // is already taken.
      response
        .setType(REGISTER_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "username is taken");
    } catch (NoResultException e) {
      // If UserRepsitory#findOne does not return NoResultException, then user with
      // specified username does not exist. Thus we can create one.

      // Creating and storing the new user
      User user = new User(username, password, Role.USER);
      dbManager.save(user);

      // Creating jsonwebtoken for the logged in user
      String loginToken = Auth.encode(user.getUsername(), Map.of("role", Role.USER));

      // Responding with OK and token
      response
        .setType(REGISTER_OK)
        .setResponseTo(request.getId())
        .putBody(BODY_TOKEN, loginToken);
    }

    return response.setType(JOIN_SERVER_OK);
  }
}

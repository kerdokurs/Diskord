package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.Role;
import diskord.server.database.user.User;
import diskord.server.init.ServerHandler;
import diskord.server.utils.CredentialValidator;
import io.netty.channel.Channel;

import static diskord.payload.PayloadBody.*;
import static diskord.payload.PayloadType.REGISTER_ERROR;
import static diskord.payload.PayloadType.REGISTER_OK;
import static diskord.payload.ResponseType.TO_SELF;
import static diskord.server.utils.credentials.CredentialConstraint.*;

public class RegisterHandler extends Handler {
  public RegisterHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    Payload response = new Payload();
    response.setResponseTo(request.getId());
    response.setResponseType(TO_SELF);
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

    String icon = (String) request.getBody().get("icon");
    //validate if the received String is valid base64
    try {
//      if (icon == null) throw new IllegalStateException("null");
//      byte[] decode = Base64.getDecoder().decode(icon);
    } catch (IllegalArgumentException err) {
      return response.setType(REGISTER_ERROR).putBody(BODY_MESSAGE, "Decoding icon String to Base64 failed.");
    }

    final boolean alreadyExists = UserTransactions.doesUserExist(dbManager, username);

    if (alreadyExists) {
      // Trying to find existing user. When it's found alert user that the username
      // is already taken.

      return response
        .setType(REGISTER_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "username is taken");
    }
    // Creating and storing the new user
    User user = new User(username, password, Role.USER, icon);
    dbManager.save(user);

    // Creating jsonwebtoken for the logged in user
    //TODO: Role.USER deprecated, no longer only one role per user
    String loginToken = Auth.encode(user.getUsername());

    // Responding with OK and token
    response
      .setType(REGISTER_OK)
      .setResponseTo(request.getId())
      .putBody(BODY_TOKEN, loginToken);

    return response;
  }
}

package diskord.server.controllers;

import diskord.server.crypto.Hash;
import diskord.server.crypto.JWT;
import diskord.server.database.DatabaseManager;
import diskord.server.database.user.Role;
import diskord.server.database.user.User;
import diskord.server.database.user.UserRepository;
import diskord.server.payload.Payload;
import diskord.server.utils.CredentialValidator;

import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static diskord.server.payload.PayloadBody.*;
import static diskord.server.payload.PayloadType.*;
import static diskord.server.utils.credentials.CredentialConstraint.*;

public class AuthenticationController {
  public static Payload handleSignUp(@NotNull final Payload request) {
    Payload response = new Payload();
    response.setResponseTo(request.getId());

    // Getting register data from the request body
    String username = (String) request.getBody().get(BODY_USERNAME);
    String password = (String) request.getBody().get(BODY_PASSWORD);

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
      DatabaseManager.userRepository().findOne(username);
      response
        .setType(REGISTER_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "username is taken");
    } catch (NoResultException e) {
      // If UserRepsitory#findOne does not return NoResultException, then user with
      // specified username does not exist. Thus we can create one.

      // Creating and storing the new user
      User user = new User(username, password, Role.USER);
      DatabaseManager.userRepository().save(user);

      // Creating jsonwebtoken for the logged in user
      String loginToken = JWT.sign(
        user.getId().toString(), Map.of("role", user.getRole())
      );

      // Responding with OK and token
      response
        .setType(REGISTER_OK)
        .setResponseTo(request.getId())
        .putBody(BODY_TOKEN, loginToken);
    }

    return response;

  }

  /**
   * Method for handling signing in
   * TODO: Tests
   *
   * @param request
   * @return
   */
  public static Payload handleSignIn(@NotNull final Payload request) {
    Payload response = new Payload();
    response.setResponseTo(request.getId());

    // Getting submitted data from the payload body
    String username = (String) request.getBody().get(BODY_USERNAME);
    String password = (String) request.getBody().get(BODY_PASSWORD);

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
      final User user = DatabaseManager.userRepository().findOne(username);

      // Hashing provided password to compare against the one in database
      final String hashedPassword = Hash.hash(password);

      // Comparing the passwords
      if (user.getPassword().equals(hashedPassword)) {
        // Login was successful, generating jsonwebtoken
        String loginToken = JWT.sign(
          user.getId().toString(), Map.of("role", user.getRole())
        );

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
        .putBody(BODY_MESSAGE, "Did not find the user.");
    }

    return response;
  }
}

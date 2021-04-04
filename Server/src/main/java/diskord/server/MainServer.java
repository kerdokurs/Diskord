package diskord.server;

import diskord.server.crypto.Hash;
import diskord.server.crypto.JWT;
import diskord.server.database.user.Role;
import diskord.server.database.user.User;
import diskord.server.payload.Payload;
import diskord.server.payload.PayloadType;
import diskord.server.utils.CredentialVerifier;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;

import static diskord.server.payload.PayloadBody.*;
import static diskord.server.payload.PayloadType.*;
import static diskord.server.utils.credentials.CredentialConstraint.*;

public class MainServer extends Server {
  public MainServer(final int port) {
    super(port);
  }

  public static void main(String[] args) throws IOException {
    final Server server = new MainServer(8192);
    server.start();
  }

  @Override
  protected void handlePayload(final Payload payload, final SelectionKey key) throws ClosedChannelException {
    final SocketChannel socketChannel = (SocketChannel) key.channel();
    final Payload response;

    switch (payload.getType()) {
      case BINK:
        response = new Payload()
          .setType(BONK);
//        stop(); saab binki ja saadab bonki ja paneb kinni
        break;
      case INFO: // TODO: Handle this more thoroughly
        response = new Payload()
          .setType(INFO)
          .putBody("server", "main");
        break;
      case LOGIN:
        response = handleLogin(payload);
        //if login OK, response type LOGIN_OK


        //check if exists in payload
        //if login not OK, response type LOGIN_ERROR
        break;
      case REGISTER:
        response = handleRegister(payload);
        break;

      default:
        response =
          new Payload()
            .setType(INVALID)
            .setResponseTo(payload.getId())
            .putBody(BODY_MESSAGE, BODY_INVALID);

    }

    socketMap.get(socketChannel).add(response);
    socketChannel.register(selector, SelectionKey.OP_WRITE);
  }

  private Payload handleRegister(Payload payload) {
    Payload response = new Payload();
    response.setResponseTo(payload.getId());
    String username = (String) payload.getBody().get(BODY_USERNAME);
    String password = (String) payload.getBody().get(BODY_PASSWORD);

    final String usernameError = CredentialVerifier.verify(
      username,
      NULL_CONSTRAINT,
      TOO_SHORT_CONSTRAINT,
      TOO_LONG_CONSTRAINT);

    if (usernameError != null) {
      return response
        .setType(REGISTER_ERROR)
        .putBody(BODY_FIELD, BODY_USERNAME)
        .putBody(BODY_MESSAGE, usernameError);
    }

    final String passwordError = CredentialVerifier.verify(
      password,
      NULL_CONSTRAINT,
      TOO_SHORT_CONSTRAINT,
      TOO_LONG_CONSTRAINT);

    if (passwordError != null) {
      return response
        .setType(REGISTER_ERROR)
        .putBody(BODY_FIELD, BODY_PASSWORD)
        .putBody(BODY_MESSAGE, passwordError);
    }

    try {
      //if user exists, cannot register new one
      dbManager.getUserRepository().findOne(username);
      response
        .setType(REGISTER_ERROR)
        .setResponseTo(payload.getId())
        .putBody(BODY_MESSAGE, "User already exists.");

    } catch (NoResultException e) {
      User user = new User(username, password, Role.USER);
      dbManager.getUserRepository().save(user);
      String loginToken = JWT.sign(
        user.getId().toString(), Map.of("role", user.getRole())
      );

      response
        .setType(REGISTER_OK)
        .setResponseTo(payload.getId())
        .putBody(BODY_TOKEN, loginToken);
    }

    return response;
  }

  private Payload handleLogin(Payload payload) {
    Payload response = new Payload();
    response.setResponseTo(payload.getId());
    String username = (String) payload.getBody().get(BODY_USERNAME);
    String password = (String) payload.getBody().get(BODY_PASSWORD);

    String loginUsernameError = CredentialVerifier.verify(
      username,
      NULL_CONSTRAINT
    );
    String loginPasswordError = CredentialVerifier.verify(
      password,
      NULL_CONSTRAINT
    );

    if (loginUsernameError != null) {
      return response
        .setType(LOGIN_ERROR)
        .putBody(BODY_FIELD, BODY_USERNAME)
        .putBody(BODY_MESSAGE, loginUsernameError);
    }

    if (loginPasswordError != null) {
      return response
        .setType(LOGIN_ERROR)
        .putBody(BODY_FIELD, BODY_PASSWORD)
        .putBody(BODY_MESSAGE, loginPasswordError);
    }


    try {
      final User user = dbManager.getUserRepository().findOne(username);
      final String hashedPassword = Hash.hash(password);
      if (user.getPassword().equals(hashedPassword)) {
        String loginToken = JWT.sign(
          user.getId().toString(), Map.of("role", user.getRole())
        );
        response
          .setType(LOGIN_OK)
          .putBody(BODY_TOKEN, loginToken);

      } else {
        response
          .setType(LOGIN_ERROR)
          .putBody(BODY_MESSAGE, "Wrong password!");
      }
    } catch (NoResultException e) {
      e.printStackTrace(); //delete when done
      response
        .setType(LOGIN_ERROR)
        .putBody(BODY_MESSAGE, "Did not find the user.");
    }

    return response;
  }
}

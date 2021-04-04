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
          .setResponseTo(payload.getId())
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
            .setType(PayloadType.INVALID)
            .setResponseTo(payload.getId())
            .putBody("message", "invalid request");

    }

    socketMap.get(socketChannel).add(response);
    socketChannel.register(selector, SelectionKey.OP_WRITE);
  }

  private Payload handleRegister(Payload payload) {
    Payload response = new Payload();
    response.setResponseTo(payload.getId());
    String username = (String) payload.getBody().get("username");
    String password = (String) payload.getBody().get("password");

    final String usernameError = CredentialVerifier.verify(
      username,
      NULL_CONSTRAINT,
      TOO_SHORT_CONSTRAINT,
      TOO_LONG_CONSTRAINT);

    if (usernameError != null) {
      return response
        .setType(REGISTER_ERROR)
        .putBody("field", "username")
        .putBody("message", usernameError);
    }

    final String passwordError = CredentialVerifier.verify(
      password,
      NULL_CONSTRAINT,
      TOO_SHORT_CONSTRAINT,
      TOO_LONG_CONSTRAINT);

    if (passwordError != null) {
      return response
        .setType(REGISTER_ERROR)
        .putBody("field", "password")
        .putBody("message", passwordError);
    }

    try {
      //if user exists, cannot register new one
      dbManager.getUserRepository().findOne(username);
      response
        .setType(REGISTER_ERROR)
        .setResponseTo(payload.getId())
        .putBody("message", "User already exists.");

    } catch (NoResultException e) {
      User user = new User(username, password, Role.USER);
      dbManager.getUserRepository().save(user);
      String loginToken = JWT.sign(
        user.getId().toString(), Map.of("role", user.getRole())
      );

      response
        .setType(REGISTER_OK)
        .setResponseTo(payload.getId())
        .putBody("token", loginToken);
    }

    return response;
  }

  private Payload handleLogin(Payload payload) {
    Payload response = new Payload();
    String username = (String) payload.getBody().get("username");
    String password = (String) payload.getBody().get("password");

    if (username != null && password != null) {
      try {
        final User user = dbManager.getUserRepository().findOne(username);
        final String hashedPassword = Hash.hash(password);
        if (user.getPassword().equals(hashedPassword)) {
          String loginToken = JWT.sign(
            user.getId().toString(), Map.of("role", user.getRole())
          );
          response
            .setType(LOGIN_OK)
            .setResponseTo(payload.getId())
            .putBody("token", loginToken);

        } else {
          response
            .setType(LOGIN_ERROR)
            .setResponseTo(payload.getId())
            .putBody("message", "Wrong password!");
        }
      } catch (NoResultException e) {
        e.printStackTrace(); //delete when done
        response
          .setType(LOGIN_ERROR)
          .setResponseTo(payload.getId())
          .putBody("message", "Did not find the user.");
      }


    } else {
      response
        .setType(LOGIN_ERROR)
        .setResponseTo(payload.getId())
        .putBody("message", "username or password does not exist.");
    }
    return response;
  }
}

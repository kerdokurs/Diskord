package diskord.server;

import diskord.payload.Payload;
import diskord.server.controllers.AuthenticationController;
import diskord.server.controllers.ChatController;
import diskord.server.database.DatabaseManager;

import static diskord.payload.PayloadBody.BODY_INVALID;
import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.*;

public class MainServer extends Server {
  public MainServer(final int port) {
    super(port, new DatabaseManager());
  }

  public static void main(String[] args) {
    final Server server = new MainServer(8192);

    final Thread mainServerThread = new Thread(server, "Main server");
    mainServerThread.start();
  }

  @Override
  protected Payload handlePayload(final Payload payload) {
    final Payload response;

    logger.info(() -> payload);

    switch (payload.getType()) {
      case BINK:
        response = new Payload()
          .setType(BONK);
        break;
      case INFO: // TODO: Handle this more thoroughly
        response = new Payload()
          .setType(INFO)
          .putBody("server", "main");
        break;
      case LOGIN:
        response = AuthenticationController.handleSignIn(dbManager, payload);
        break;
      case REGISTER:
        response = AuthenticationController.handleSignUp(dbManager, payload);
        break;
      case MSG:
        response = ChatController.handleMessage(dbManager, payload);
        break;
      default:
        response = handleInvalidRequest(payload);
    }

    return response;
  }

  private Payload handleInvalidRequest(final Payload request) {
    return new Payload()
      .setType(INVALID)
      .setResponseTo(request.getId())
      .putBody(BODY_MESSAGE, BODY_INVALID);
  }
}

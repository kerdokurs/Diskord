package diskord.server;

import diskord.server.controllers.AuthenticationController;
import diskord.server.payload.Payload;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static diskord.server.payload.PayloadBody.BODY_INVALID;
import static diskord.server.payload.PayloadBody.BODY_MESSAGE;
import static diskord.server.payload.PayloadType.*;

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
      default:
        response = handleInvalidRequest(payload);
    }

    socketMap.get(socketChannel).add(response);
    socketChannel.register(selector, SelectionKey.OP_WRITE);
  }

  private Payload handleInvalidRequest(final Payload request) {
    return new Payload()
      .setType(INVALID)
      .setResponseTo(request.getId())
      .putBody(BODY_MESSAGE, BODY_INVALID);
  }
}

package diskord.server;

import diskord.server.payload.Payload;
import diskord.server.payload.PayloadType;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

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
          .setType(PayloadType.BONK)
          .setId(UUID.randomUUID());
        stop();
        break;
      case INFO: // TODO: Handle this more thoroughly
        response = new Payload()
          .setType(PayloadType.INFO)
          .setResponseTo(payload.getId())
          .putBody("server", "main")
          .setId(UUID.randomUUID());
        break;
      default:
        response =
          new Payload()
            .setType(PayloadType.INVALID)
            .setResponseTo(payload.getId())
            .putBody("message", "invalid request")
            .setId(UUID.randomUUID());
    }

    socketMap.get(socketChannel).add(response);
    socketChannel.register(selector, SelectionKey.OP_WRITE);
  }
}

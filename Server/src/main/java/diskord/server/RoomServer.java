package diskord.server;

import diskord.server.controllers.ChatController;
import diskord.server.database.DatabaseManager;
import diskord.payload.Payload;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class RoomServer extends Server {
  // TODO: Should a room server know which room its hosting???

  protected RoomServer(final int port, final DatabaseManager dbManager) {
    super(port, dbManager);
  }

  // TODO: should we do this on the main server aswell?
  // then we would not have to manage all the room servers on many threads
  // also, the performance should not take a huge hit since the nio socketchannels
  // can handle big payload capacities.
  @Override
  protected void handlePayload(final Payload payload, final SelectionKey key) throws ClosedChannelException {
    final SocketChannel socketChannel = (SocketChannel) key.channel();

    final Payload response;

    switch (payload.getType()) {
      case MSG:
        response = ChatController.handleMessage(dbManager, payload);
        break;
      default:
        response = unhandledPayload(payload, key);
    }

    socketMap.get(socketChannel).add(response);
    socketChannel.register(selector, SelectionKey.OP_WRITE);
  }
}

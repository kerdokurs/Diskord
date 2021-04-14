package diskord.server;

import diskord.server.payload.Payload;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

public class RoomServer extends Server {
  protected RoomServer(final int port) {
    super(port);
  }

  @Override
  protected void handlePayload(final Payload payload, final SelectionKey key) throws ClosedChannelException {

  }
}

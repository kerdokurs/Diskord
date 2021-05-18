package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.server.database.DatabaseManager;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import static diskord.payload.PayloadType.BONK;
import static diskord.payload.ResponseType.TO_SELF;

public class BinkHandler extends Handler {
  public BinkHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    return new Payload()
      .setType(BONK)
      .setResponseType(TO_SELF)
      .setResponseTo(request.getId());
  }
}

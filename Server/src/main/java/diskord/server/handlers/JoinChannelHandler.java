package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.server.database.DatabaseManager;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import java.util.List;

import static diskord.payload.PayloadType.JOIN_CHANNEL_ERROR;
import static diskord.payload.PayloadType.JOIN_CHANNEL_OK;
import static diskord.payload.ResponseType.TO_SELF;

public class JoinChannelHandler extends Handler {
  public JoinChannelHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    return new Payload()
      .setResponseTo(request.getId())
      .setResponseType(TO_SELF)
      .setType(request.getBody().containsKey("channel_id") ? JOIN_CHANNEL_OK : JOIN_CHANNEL_ERROR)
      .putBody("users", List.of());
  }
}

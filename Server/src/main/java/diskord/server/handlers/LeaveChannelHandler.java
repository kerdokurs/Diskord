package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.server.database.DatabaseManager;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import java.util.List;

import static diskord.payload.PayloadType.*;
import static diskord.payload.ResponseType.TO_SELF;

public class LeaveChannelHandler extends Handler {
  public LeaveChannelHandler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  @Override
  public Payload handleRequest(final Payload request, final Channel channel) {
    return new Payload()
      .setResponseTo(request.getId())
      .setResponseType(TO_SELF)
      .setType(request.getBody().containsKey("channel_id") ? LEAVE_CHANNEL_OK : LEAVE_CHANNEL_ERROR)
      .putBody("users", List.of());
  }
}

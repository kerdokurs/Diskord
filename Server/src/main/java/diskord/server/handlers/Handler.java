package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.server.database.DatabaseManager;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

public abstract class Handler {
  protected final DatabaseManager dbManager;
  protected final ServerHandler serverHandler;

  protected Handler(final DatabaseManager dbManager, final ServerHandler serverHandler) {
    this.dbManager = dbManager;
    this.serverHandler = serverHandler;
  }

  /**
   * Method which each handler must override that handles a specific type of request.
   * For example: LOGIN, REGISTER or MSG.
   *
   * @param request incoming request
   * @return response to that request
   */
  public abstract Payload handleRequest(final Payload request, final Channel channel);
}

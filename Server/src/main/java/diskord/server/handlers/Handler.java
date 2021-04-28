package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.server.database.DatabaseManager;

import javax.validation.constraints.NotNull;

public abstract class Handler {
  protected final DatabaseManager dbManager;

  protected Handler(final DatabaseManager dbManager) {
    this.dbManager = dbManager;
  }

  /**
   * Method which each handler must override that handles a specific type of request.
   * For example: LOGIN, REGISTER or MSG.
   *
   * @param request incoming request
   * @return response to that request
   */
  public abstract Payload handleRequest(@NotNull final Payload request);
}

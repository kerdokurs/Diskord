package diskord.server.handlers;

import diskord.payload.Payload;
import diskord.server.database.DatabaseManager;

import javax.validation.constraints.NotNull;

public class RegisterHandler extends Handler {
  public RegisterHandler(final DatabaseManager dbManager) {
    super(dbManager);
  }

  @Override
  public Payload handleRequest(final @NotNull Payload request) {
    return new Payload();
  }
}

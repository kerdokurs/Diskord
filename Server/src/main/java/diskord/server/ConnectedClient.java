package diskord.server;

import io.netty.channel.Channel;
import lombok.Getter;

import java.util.UUID;

public class ConnectedClient {
  @Getter
  private final UUID userId;

  @Getter
  private final Channel channel;

  public ConnectedClient(final UUID userId, final Channel channel) {
    this.userId = userId;
    this.channel = channel;
  }
}

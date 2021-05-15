package diskord.server.room;

import lombok.Getter;

import java.util.UUID;

public class Channel {
  @Getter
  private UUID id;

  @Getter
  private String name;

  @Getter
  private String iconBase64;

  public Channel(final UUID id, final String name, final String iconBase64) {
    this.id = id;
    this.name = name;
    this.iconBase64 = iconBase64;
  }

  public static Channel map(final diskord.server.database.channel.Channel channel) {
    return new Channel(channel.getId(), channel.getName(), channel.getIcon());
  }
}

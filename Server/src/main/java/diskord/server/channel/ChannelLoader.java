package diskord.server.channel;

import diskord.server.database.DatabaseManager;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelLoader {
  public static List<Channel> loadChannels(final DatabaseManager dbManager) {
    // Siin laeme kanalid andmebaasist ja muudame sobivasse formaati ehk andmebaasi kanal -> serveri kanal.
    return dbManager.getAll(Channel.class)
      .stream()
      .map(channel ->
        new Channel()
          .setName(channel.getName())
          .setId(channel.getId())
      ).collect(Collectors.toList());
  }
}

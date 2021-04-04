package diskord.server.channel;

import diskord.server.database.channel.ChannelRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelLoader {
  public static List<Channel> loadChannels(final ChannelRepository channelRepository) {
    // Siin laeme kanalid andmebaasist ja muudame sobivasse formaati ehk andmebaasi kanal -> serveri kanal.
    return channelRepository.findAll()
        .stream()
        .map(channel ->
            new Channel()
                .setName(channel.getName())
                .setId(channel.getId())
        ).collect(Collectors.toList());
  }
}

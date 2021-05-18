package diskord.server.dto;

import diskord.payload.dto.ChannelDTO;
import diskord.server.database.channel.Channel;
import org.modelmapper.ModelMapper;

public class ConvertChannel {
  public static ChannelDTO convert(final ModelMapper mapper, final Channel channel) {
    return mapper.map(channel, ChannelDTO.class)
      .setUuid(channel.getId())
      .setBase64Icon(channel.getIcon());
  }
}

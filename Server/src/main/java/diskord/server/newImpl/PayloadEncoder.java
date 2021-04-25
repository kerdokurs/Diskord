package diskord.server.newImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.payload.Payload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.Objects;

public class PayloadEncoder extends MessageToMessageEncoder<Payload> {
  private final ObjectMapper mapper;

  public PayloadEncoder(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  protected void encode(final ChannelHandlerContext channelHandlerContext, final Payload payload, final List<Object> list) throws Exception {
    if(!Objects.isNull(payload)) {
      list.add(
        payload.toJson(mapper)
      );
    }
  }
}

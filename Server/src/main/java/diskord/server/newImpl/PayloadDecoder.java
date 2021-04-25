package diskord.server.newImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.payload.Payload;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class PayloadDecoder extends MessageToMessageDecoder<ByteBuf> {
  private final ObjectMapper mapper;

  public PayloadDecoder(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf buf, final List<Object> list) throws Exception {
    final Payload payload = Payload.fromJson(mapper, buf.toString(Charset.defaultCharset()));
    list.add(payload);
  }
}

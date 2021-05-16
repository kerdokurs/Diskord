package diskord.client.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.client.ServerConnection;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
  private  ServerConnection serverConnection;
  private ObjectMapper mapper;
  public ClientInitializer(ServerConnection serverConnection, ObjectMapper mapper) {
    this.serverConnection = serverConnection;
    this.mapper = mapper;
  }

  @Override
  protected void initChannel(final SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();

    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
    pipeline.addLast("encoder", new StringEncoder());
    pipeline.addLast("decoder", new StringDecoder());
    pipeline.addLast("handler", new ClientHandler(serverConnection, mapper));
  }
}
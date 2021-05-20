package diskord.server.init;

import diskord.server.Server;
import diskord.server.database.DatabaseManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
  private final Server server;

  public ServerInitializer(final Server server) {
    this.server = server;
  }

  // Initializing the pipeline for a socket channel
  @Override
  protected void initChannel(final SocketChannel socketChannel) {
    ChannelPipeline pipeline = socketChannel.pipeline();

    pipeline.addLast("timeout", new ReadTimeoutHandler(10));

    pipeline.addLast("framer", new DelimiterBasedFrameDecoder((int) 1e9, Delimiters.lineDelimiter()));
    pipeline.addLast("encoder", new StringEncoder());
    pipeline.addLast("decoder", new StringDecoder());

    pipeline.addLast("handler", new ServerHandler(server));
  }
}
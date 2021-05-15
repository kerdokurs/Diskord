package diskord.server.init;

import diskord.server.database.DatabaseManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
  private final DatabaseManager dbManager;

  public ServerInitializer(final DatabaseManager dbManager) {
    this.dbManager = dbManager;
  }

  // Initializing the pipeline for a socket channel
  @Override
  protected void initChannel(final SocketChannel socketChannel) {
    ChannelPipeline pipeline = socketChannel.pipeline();

    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8912, Delimiters.lineDelimiter()));
    pipeline.addLast("encoder", new StringEncoder());
    pipeline.addLast("decoder", new StringDecoder());

    pipeline.addLast("handler", new ServerHandler(dbManager));
  }
}
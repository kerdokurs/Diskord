package diskord.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Siin seadistame serveri adapteri (kanali pmst)
 */
public class ServerAdapterInitializer extends ChannelInitializer<SocketChannel> {
  /**
   * Seadistame kanali
   *
   * @param ch kanal
   */
  @Override
  protected void initChannel(final SocketChannel ch) {
    final ChannelPipeline pipeline = ch.pipeline();

    // lisame dekoodri ja enkoodri
    pipeline.addLast("decoder", new StringDecoder());
    pipeline.addLast("encoder", new StringEncoder());

    // lõpuks handleri
    pipeline.addLast("handler", new ServerAdapterHandler());
  }
}

package diskord.server.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;

/**
 * See klass on lihtsalt testimiseks, aga kliendi põhimõte jääb arvatavasti üsna samaks.
 */
public class TestClient {
  public static void main(final String[] args) throws Exception {
    final EventLoopGroup group = new NioEventLoopGroup();

    try {
      final Bootstrap bootstrap = new Bootstrap().group(group)
          .channel(NioSocketChannel.class)
          .handler(new ClientInitializer());

      final Channel channel = bootstrap.connect(InetAddress.getLocalHost(), 8192).sync().channel();

      channel.write("test1\n");
      channel.write("test2\n");
      channel.flush();
    } finally {
      group.shutdownGracefully().sync();
    }
  }
}

class ClientInitializer extends ChannelInitializer<Channel> {
  @Override
  protected void initChannel(final Channel ch) {
    final ChannelPipeline pipeline = ch.pipeline();

    pipeline.addLast("decoder", new StringDecoder());
    pipeline.addLast("encoder", new StringEncoder());

    pipeline.addLast("handler", new ClientHandler());
  }
}

class ClientHandler extends SimpleChannelInboundHandler<String> {
  @Override
  public void channelActive(final ChannelHandlerContext channelHandlerContext) {
    channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("bink", CharsetUtil.UTF_8));
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
    System.out.println(msg);
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final String msg) {
    System.out.printf("client received: %s%n", msg);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext channelHandlerContext, final Throwable cause) {
    cause.printStackTrace();
    channelHandlerContext.close();
  }
}

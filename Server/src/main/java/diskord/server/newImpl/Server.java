package diskord.server.newImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Objects;

public class Server {
  private final int port;

  public Server(final int port) {
    this.port = port;
  }

  public static void main(String[] args) {
    new Server(9000).run();
  }

  public void run() {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap()
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ServerInitializer());

      bootstrap.bind(port).sync().channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}

class ServerInitializer extends ChannelInitializer<SocketChannel> {
  @Override
  protected void initChannel(final SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();

    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8912, Delimiters.lineDelimiter()));
    pipeline.addLast("encoder", new StringEncoder());
    pipeline.addLast("decoder", new StringDecoder());

    pipeline.addLast("handler", new ServerHandler());
  }
}

class ServerHandler extends SimpleChannelInboundHandler<String> {
  private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  @Override
  public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
    Channel incoming = ctx.channel();

    for (Channel channel : channels) {
      channel.writeAndFlush("[SERVER] " + incoming.remoteAddress() + " has joined\n");
    }

    channels.add(incoming);
  }

  @Override
  public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
    Channel incoming = ctx.channel();

    for (Channel channel : channels) {
      channel.writeAndFlush("[SERVER] " + incoming.remoteAddress() + " has left\n");
    }

    channels.remove(incoming);
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final String s) throws Exception {
    Channel incoming = channelHandlerContext.channel();
    System.out.printf("%s: %s%n", incoming, s);

    for (Channel channel : channels) {
      if (!Objects.equals(channel, incoming)) {
        channel.writeAndFlush("[" + incoming.remoteAddress() + "] " + s + "\n");
      }
    }
  }
}
package diskord.server.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class TestClient {
  public static void main(final String[] args) throws Exception {
    final EventLoopGroup group = new NioEventLoopGroup();

    try {
      final Bootstrap clientBootstrap = new Bootstrap();

      clientBootstrap.group(group);
      clientBootstrap.channel(NioSocketChannel.class);
      clientBootstrap.remoteAddress(new InetSocketAddress("localhost", 8192));

      clientBootstrap.handler(new ChannelInitializer<>() {
        @Override
        protected void initChannel(final Channel ch) {
          ch.pipeline().addLast(new ClientHandler());
        }
      });

      final ChannelFuture channelFuture = clientBootstrap.connect().sync();
      channelFuture.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }
}

class ClientHandler extends SimpleChannelInboundHandler {
  @Override
  public void channelActive(final ChannelHandlerContext channelHandlerContext) {
    channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("Netty Rocks!", CharsetUtil.UTF_8));
  }

  @Override
  public void channelRead0(final ChannelHandlerContext channelHandlerContext, final Object in) {
    final ByteBuf buf = (ByteBuf) in;
    System.out.printf("client received: %s%n", buf.toString(CharsetUtil.UTF_8));
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext channelHandlerContext, final Throwable cause) {
    cause.printStackTrace();
    channelHandlerContext.close();
  }
}

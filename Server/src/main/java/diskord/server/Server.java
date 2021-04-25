package diskord.server;

import diskord.server.init.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
  private final int port;

  public Server(final int port) {
    this.port = port;
  }

  public static void main(String[] args) {
    final Server server = new Server(8192);
    server.run();
  }

  public void run() {
    final EventLoopGroup bossGroup = new NioEventLoopGroup();
    final EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap()
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ServerInitializer());

      bootstrap.bind(port).sync().channel().closeFuture().sync();
    } catch (final InterruptedException e) {
      // TODO: Handle
      e.printStackTrace();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}

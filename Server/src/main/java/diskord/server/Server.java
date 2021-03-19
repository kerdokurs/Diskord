package diskord.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.logging.Logger;

public class Server {
  private final int port;

  // Logimiseks System.out.println()-i ei kasuta!
  private final Logger logger = Logger.getLogger("server");

  public Server(final int port) {
    this.port = port;
  }

  public static void main(final String[] args) {
    new Server(8192).start();
  }

  /**
   * Alustame serveri
   */
  public void start() {
    final EventLoopGroup producer = new NioEventLoopGroup();
    final EventLoopGroup consumer = new NioEventLoopGroup();

    try {
      final ServerBootstrap bootstrap = new ServerBootstrap()
          .group(producer, consumer)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ServerAdapterInitializer());

      logger.info("server has started");

      bootstrap.bind(port).sync().channel().closeFuture().sync();
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      producer.shutdownGracefully();
      consumer.shutdownGracefully();
    }
  }
}

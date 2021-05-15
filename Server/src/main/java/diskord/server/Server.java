package diskord.server;

import diskord.server.database.DatabaseManager;
import diskord.server.init.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;

public class Server {
  private final int port;

  private final DatabaseManager dbManager;

  public Server(int port) {
    this.port = port;
    dbManager = new DatabaseManager();
  }

  /**
   * Main entry point
   */
  public static void main(final String[] args) throws InterruptedException {
    Server server = new Server(8192);
    server.run();
  }

  public void run() throws InterruptedException {
    // Don't ask me what all of this does. All I know it
    // just works. See netty.io docs if more interested.

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap bootstrap = new ServerBootstrap()
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ServerInitializer(dbManager));

      bootstrap.bind(port).sync().channel().closeFuture().sync();
    } catch (InterruptedException e) {
      // TODO: Decide exactly what to do
      LogManager.getLogger(getClass().getName()).error(() -> "InterruptException", e);
      throw e;
    } finally {
      dbManager.close();
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}

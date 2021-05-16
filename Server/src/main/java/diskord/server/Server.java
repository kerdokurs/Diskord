package diskord.server;

import diskord.server.database.DatabaseManager;
import diskord.server.init.ServerInitializer;
import diskord.server.room.Room;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class Server {
  private final int port;

  @Getter
  private final DatabaseManager dbManager;

  @Getter
  private List<Room> rooms; // created rooms (servers) with their channels

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
    rooms = Room.loadRooms(dbManager);
    final EventLoopGroup bossGroup = new NioEventLoopGroup();
    final EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      final ServerBootstrap bootstrap = new ServerBootstrap()
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ServerInitializer(this));

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

package diskord.server;

import diskord.server.jpa.user.UserRepository;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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

    EntityManager em = null;

    try {
      final ServerBootstrap bootstrap = new ServerBootstrap()
          .group(producer, consumer)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ServerAdapterInitializer());

      logger.info("server has started");

      final EntityManagerFactory emf = Persistence.createEntityManagerFactory("DiskordServer.database");
      em = emf.createEntityManager();
      final UserRepository userRepository = new UserRepository(em);

      System.out.println(userRepository.findAll());

      bootstrap.bind(port).sync().channel().closeFuture().sync();
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      if (em != null) em.close();

      producer.shutdownGracefully();
      consumer.shutdownGracefully();
    }
  }
}

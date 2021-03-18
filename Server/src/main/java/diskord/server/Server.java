package diskord.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class Server {
  public Server() {
    final EventLoopGroup group = new NioEventLoopGroup();

    try {
      final ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(group);
      bootstrap.channel(NioServerSocketChannel.class);
      bootstrap.localAddress(new InetSocketAddress("localhost", 8192));

      bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(final SocketChannel ch) {
          ch.pipeline().addLast(new ClientHandler());
        }
      });

      final ChannelFuture cfuture = bootstrap.bind().sync();
      cfuture.channel().closeFuture().sync();
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      group.shutdownGracefully();
    }
//    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("DiskordServer.database");
//
//    final UserRepository userRepository = new UserRepository(factory);
//    System.out.println(userRepository.findAll());
//
//    final LoginController loginController = new LoginController(userRepository);
//
//    try {
//      final String jws = loginController.handleLogin("kerdo", "testing");
//      System.out.println(jws);
//
//      System.out.println(JWT.validate(jws));
//    } catch (final NotFoundException e) {
//      System.out.println(e.getMessage());
//    }
//
//    factory.close();
  }

  public static void main(final String[] args) {
    new Server();
  }
}

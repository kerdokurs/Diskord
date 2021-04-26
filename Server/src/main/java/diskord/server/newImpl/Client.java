package diskord.server.newImpl;

import diskord.payload.Payload;
import diskord.payload.PayloadType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
  private final String host;
  private final int port;

  public Client(final String host, final int port) {
    this.host = host;
    this.port = port;
  }

  public static void main(String[] args) {
    new Client("localhost", 9000).run();
  }

  public void run() {
    final EventLoopGroup group = new NioEventLoopGroup();

    try {
      Bootstrap bootstrap = new Bootstrap()
        .group(group)
        .channel(NioSocketChannel.class)
        .handler(new ClientInitializer());

      Channel channel = bootstrap.connect(host, port).sync().channel();
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      String line;
      while ((line = in.readLine()) != null && !line.isEmpty()) {
        Payload payload = new Payload()
          .setType(PayloadType.MSG)
          .putBody("message", line);

        channel.writeAndFlush(payload + "\r\n");
      }
    } catch (final InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class ClientInitializer extends ChannelInitializer<SocketChannel> {
  @Override
  protected void initChannel(final SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();

    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
    pipeline.addLast("encoder", new StringEncoder());
    pipeline.addLast("decoder", new StringDecoder());

    pipeline.addLast("handler", new ClientHandler());
  }
}

class ClientHandler extends SimpleChannelInboundHandler<String> {
  @Override
  protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final String s) throws Exception {
//    final Payload payload = Payload.fromJson(new ObjectMapper(), s);
//    System.out.println(payload);
    System.out.printf("received %s%n", s);
  }
}
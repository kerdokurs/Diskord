package diskord.server.newImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class Client {
  final ObjectMapper mapper = new ObjectMapper();
  private final String host;
  private final int port;
  private String jwt;
  private Channel channel;

  public Client(final String host, final int port) {
    this.host = host;
    this.port = port;
  }

  public static void main(String[] args) {
    new Client("localhost", 8192).run();
  }

  public void run() {
    final EventLoopGroup group = new NioEventLoopGroup();

    try {
      Bootstrap bootstrap = new Bootstrap()
        .group(group)
        .channel(NioSocketChannel.class)
        .handler(new ClientInitializer(this));

      channel = bootstrap.connect(host, port).sync().channel();


//      Payload request = new Payload()
//        .setType(PayloadType.REGISTER)
//        .putBody("username", "kerdo")
//        .putBody("password", "kerdo");
//
//      channel.writeAndFlush(request.toJson(mapper) + "\r\n");

      Payload request = new Payload()
        .setType(PayloadType.LOGIN)
        .putBody("username", "mannu")
        .putBody("password", "mannu");

      channel.writeAndFlush(request.toJson(mapper) + "\r\n");


//      String line;
//      while ((line = in.readLine()) != null && !line.isEmpty()) {
//        Payload payload = new Payload()
//          .setType(PayloadType.MSG)
//          .putBody("message", line);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        final String data = payload.toJson(mapper);
//        System.out.println(data);
//        channel.writeAndFlush(data + "\r\n");
//      }
    } catch (final InterruptedException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;

    Payload request = new Payload()
      .setType(PayloadType.INFO_USER_SERVERS)
      .setJwt(jwt);

    try {
      channel.writeAndFlush(request.toJson(mapper) + "\r\n");
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}

class ClientInitializer extends ChannelInitializer<SocketChannel> {
  private Client client;

  public ClientInitializer(Client client) {
    this.client = client;
  }

  @Override
  protected void initChannel(final SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();

    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(65536, Delimiters.lineDelimiter()));
    pipeline.addLast("encoder", new StringEncoder());
    pipeline.addLast("decoder", new StringDecoder());

    pipeline.addLast("handler", new ClientHandler(client));
  }
}

class ClientHandler extends SimpleChannelInboundHandler<String> {
  private final Client client;

  public ClientHandler(final Client client) {
    this.client = client;
  }

  // List<Controller> controllers;
  // Map<UUID, Controller> controllers;
  @Override
  protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final String s) throws Exception {
//    final Payload payload = Payload.fromJson(new ObjectMapper(), s);
//    System.out.println(payload);
    System.out.printf("received %s%n", s);
    Payload payload = Payload.fromJson(new ObjectMapper(), s);
    if (payload.getType().equals(PayloadType.LOGIN_OK)) client.setJwt((String) payload.getBody().get("token"));

    // controllers.handlePayload(payload);
    // Controller c = controllers.get(payload.getResponseTo());
    // if (c == null) defaultController.handlePayload(payload);
    // c.handlePayload(payload);
    // controllers.remove(payload.getResponseTo());
  }
}
package diskord.client.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.client.ServerConnection;
import diskord.payload.Payload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

  private ServerConnection serverConnection;
  private ObjectMapper objectMapper;

  public ClientHandler(ServerConnection serverConnection, ObjectMapper mapper) {
    this.serverConnection = serverConnection;
    this.objectMapper = mapper;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final String s) throws Exception {
    System.out.printf("received %s%n", s);
    Payload payload = Payload.fromJson(objectMapper, s);
    serverConnection.handlePayload(payload);

  }
}
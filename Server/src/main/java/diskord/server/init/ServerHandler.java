package diskord.server.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import diskord.server.database.DatabaseManager;
import diskord.server.handlers.Handler;
import diskord.server.handlers.LoginHandler;
import diskord.server.handlers.RegisterHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static diskord.payload.PayloadBody.BODY_INVALID;
import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.*;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
  private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  private final Logger logger = LogManager.getLogger();
  private final DatabaseManager dbManager;
  private final ObjectMapper mapper = new ObjectMapper();

  private final Map<PayloadType, Handler> handlers = new HashMap<>();

  public ServerHandler() {
    dbManager = new DatabaseManager();

    registerHandler(LOGIN, new LoginHandler(dbManager));
    registerHandler(REGISTER, new RegisterHandler(dbManager));
  }

  @Override
  public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
    // siit saame ühendunud kliendi

    Channel incoming = ctx.channel();

    logger.info(() -> String.format("%s has joined", incoming));

    for (Channel channel : channels) {
      channel.writeAndFlush("[SERVER] " + incoming.remoteAddress() + " has joined\n");
    }

    channels.add(incoming);
  }

  @Override
  public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
    // siit saame lahkunud kliendi

    Channel incoming = ctx.channel();

    logger.info(() -> String.format("%s has left", incoming));

    for (Channel channel : channels) {
      channel.writeAndFlush("[SERVER] " + incoming.remoteAddress() + " has left\n");
    }

    channels.remove(incoming);
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final String s) throws Exception {
    // siit saame sõne, mille klient saatis

    Channel incoming = channelHandlerContext.channel();

    logger.info(() -> String.format("%s: %s", incoming, s));

    final Payload request = Payload.fromJson(mapper, s);
    final Payload response;

    final Handler handler = handlers.get(request.getType());

    if (handler != null) {
      response = handler.handleRequest(request);
    } else {
      response = unhandledPayload(request);
    }

    // handling request
//    switch (request.getType()) {
//      case BINK:
//        response = new Payload()
//          .setType(BONK);
//        break;
//      case LOGIN:
//        response = AuthenticationController.handleSignIn(dbManager, request);
//        break;
//      case REGISTER:
//        response = AuthenticationController.handleSignUp(dbManager, request);
//        break;
//      case MSG:
//        response = ChatController.handleMessage(dbManager, request);
//        break;
//      default:
//        response = handleInvalidRequest(request);
//    }

    // TODO: otsustada, mida responseiga teha

    for (Channel channel : channels) {
      if (!Objects.equals(channel, incoming)) {
        // nii saame kõigile laiali saata
        channel.writeAndFlush("[" + incoming.remoteAddress() + "] " + s + "\n");
      }
    }
  }

  private Payload unhandledPayload(final Payload request) {
    return new Payload()
      .setType(INVALID)
      .setResponseTo(request.getId())
      .putBody(BODY_MESSAGE, BODY_INVALID);
  }

  public void registerHandler(final PayloadType type, final Handler handler) {
    if (handlers.get(type) != null)
      throw new InvalidParameterException(String.format("A handler already exists with type %s.", type));

    handlers.put(type, handler);
  }
}
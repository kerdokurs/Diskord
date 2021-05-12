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
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import static diskord.payload.PayloadBody.BODY_INVALID;
import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.*;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
  private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  private final Logger logger = LogManager.getLogger();
  private final DatabaseManager dbManager; // TODO: close somewhere
  private final ObjectMapper mapper = new ObjectMapper();

  private final Map<PayloadType, Handler> handlers = new EnumMap<>(PayloadType.class);

  public ServerHandler() {
    dbManager = new DatabaseManager();

    registerHandler(LOGIN, new LoginHandler(dbManager, this));
    registerHandler(REGISTER, new RegisterHandler(dbManager, this));
  }

  @Override
  public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
    Channel incoming = ctx.channel();

    logger.info(() -> String.format("incoming connection from %s", incoming));

    for (Channel channel : channels) {
      channel.writeAndFlush("[SERVER] " + incoming.remoteAddress() + " has joined\n");
    }

    channels.add(incoming);
  }

  @Override
  public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
    dbManager.close();
    super.channelUnregistered(ctx);
  }

  @Override
  public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
    Channel incoming = ctx.channel();

    logger.info(() -> String.format("%s has disconnected", incoming));

    for (Channel channel : channels) {
      channel.writeAndFlush("[SERVER] " + incoming.remoteAddress() + " has left\n");
    }

    channels.remove(incoming);
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final String s) throws Exception {
    Channel incoming = ctx.channel();

    logger.info(() -> String.format("%s: %s", incoming, s));

    final Payload request = Payload.fromJson(mapper, s);
    final Payload response;

    final Handler handler = handlers.get(request.getType());

    if (handler != null) {
      response = handler.handleRequest(request, incoming);
    } else {
      response = unhandledRequest(request);
    }

    // TODO: otsustada, mida vastusega teha

    for (Channel channel : channels) {
      if (!Objects.equals(channel, incoming)) {
        // nii saame kõigile laiali saata
        channel.writeAndFlush("[" + incoming.remoteAddress() + "] " + s + "\n");
      }
    }
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
    // TODO: What to do with the caught exception?
    logger.error(() -> "Exception has been caught", cause);
    ctx.close();
  }

  /**
   * Method for handling any unhandled payload types
   *
   * @param request incoming payload
   * @return response to incoming payload
   */
  private Payload unhandledRequest(final Payload request) {
    return new Payload()
      .setType(INVALID)
      .setResponseTo(request.getId())
      .putBody(BODY_MESSAGE, BODY_INVALID);
  }

  /**
   * Method for sending payload to channel
   *
   * @param channel channel to send to
   * @param payload payload to send
   */
  public void send(final Channel channel, final Payload payload) {
    channel.writeAndFlush(payload); // TODO: test & not working for sure
  }

  /**
   * Method for sending a payload to all but one ignored channel.
   * Let <code>ignore = null</code> to send to every channel.
   *
   * @param payload payload to send
   * @param ignore  channel to ignore
   */
  public void sendAll(final Payload payload, final Channel ignore) {
    for (final Channel channel : channels)
      if (!channel.equals(ignore))
        send(channel, payload);
  }

  /**
   * Method for sending a payload to all connected channels
   *
   * @param payload payload to send
   */
  public void sendAll(final Payload payload) {
    sendAll(payload, null);
  }

  /**
   * Method registers a handler to handle provided payload type
   *
   * @param type    payload type
   * @param handler handler to handle that payload type
   * @throws InvalidParameterException when a handler with the same type is already registered
   */
  public void registerHandler(final PayloadType type, final Handler handler) throws InvalidParameterException {
    if (handlers.get(type) != null)
      throw new InvalidParameterException(String.format("A handler already exists with type %s.", type));

    handlers.put(type, handler);
  }

  /**
   * Method unregisters a handler with provided payload type
   *
   * @param type payload type
   * @throws InvalidParameterException when type does not have any handlers
   */
  public void unregisterHandler(final PayloadType type) throws InvalidParameterException {
    if (handlers.get(type) == null)
      throw new InvalidParameterException(String.format("Handler with type %s is not registered", type));

    handlers.remove(type);
  }
}
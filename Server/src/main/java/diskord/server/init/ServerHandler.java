package diskord.server.init;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import diskord.server.ConnectedClient;
import diskord.server.Server;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.dto.ConvertUser;
import diskord.server.handlers.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import static diskord.payload.PayloadBody.BODY_INVALID;
import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.*;
import static diskord.payload.ResponseType.TO_ALL_EXCEPT_SELF;
import static diskord.payload.ResponseType.TO_SELF;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
  private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  private final Server server;
  private final DatabaseManager dbManager;
  private final Logger logger = LogManager.getLogger();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ModelMapper modelMapper = new ModelMapper();

  private final Map<PayloadType, Handler> handlers = new EnumMap<>(PayloadType.class);

  public ServerHandler(final Server server) {
    this.server = server;
    dbManager = server.getDbManager();

    registerHandler(MSG, new MessageHandler(dbManager, this));
    registerHandler(BINK, new BinkHandler(dbManager, this));
    registerHandler(LOGIN, new LoginHandler(dbManager, this));
    registerHandler(REGISTER, new RegisterHandler(dbManager, this));
    registerHandler(JOIN_SERVER, new JoinServerHandler(dbManager, this));
    registerHandler(INFO_USER_SERVERS, new UserInfoServersHandler(dbManager, this));
    registerHandler(INFO_CHANNELS, new InfoChannelsHandler(dbManager, this));

    registerHandler(JOIN_CHANNEL, new JoinChannelHandler(dbManager, this));
    registerHandler(LEAVE_CHANNEL, new LeaveChannelHandler(dbManager, this));

//    final User user = UserTransactions.getUserByUsername(dbManager, "kerdo");
//    System.out.println(user);
//    final List<JoinedServer> userJoinedRooms = UserTransactions.getUserJoinedRooms(dbManager, user);
//    for (final JoinedServer userJoinedRoom : userJoinedRooms) {
//      System.out.printf("Deleted room %s%n", userJoinedRoom);
//      dbManager.delete(userJoinedRoom);
//    }
//
//    final List<Room> rooms = RoomTransactions.getRooms(dbManager);
//    for (final Room room : rooms) {
//      System.out.printf("Deleted room %s%n", room);
//      dbManager.delete(room);
//    }
  }

  @Override
  public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
    Channel incoming = ctx.channel();

    logger.info("incoming connection from {}", incoming);

    // for (Channel channel : channels) {
    //   channel.writeAndFlush("[SERVER] " + incoming.remoteAddress() + " has joined\n");
    // }

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

    logger.info("{} has disconnected", incoming);

    //for (Channel channel : channels) {
    //  channel.writeAndFlush("[SERVER] " + incoming.remoteAddress() + " has left\n");
    //}

    channels.remove(incoming);
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final String s) throws Exception {
    Channel incoming = ctx.channel();

    logger.info("{}: {}", incoming, s);

    final Payload request = Payload.fromJson(objectMapper, s);
    final Payload response;

    final Handler handler = handlers.get(request.getType());

    if (handler != null) {
      response = handler.handleRequest(request, incoming);
    } else {
      response = unhandledRequest(request);
    }

    // Handling additional logic when a user joins OK
    if (request.getType().equals(JOIN_CHANNEL) && response.getType().equals(JOIN_CHANNEL_OK))
      handleUsersOnJoin(incoming, request, response);

    // Handling additional logic when a user leaves OK
    if (request.getType().equals(LEAVE_CHANNEL) && response.getType().equals(LEAVE_CHANNEL_OK))
      handleUsersOnLeave(incoming, request);

    if (request.getType().equals(MSG) && response.getType().equals(MSG)) {
      final Payload msgOkPayload = new Payload()
        .setType(MSG_OK)
        .setResponseType(TO_SELF)
        .setResponseTo(request.getId());
      send(incoming, msgOkPayload);
    }

    System.out.println(server.getChannelJoinedChannels());

    switch (response.getResponseType()) {
      case TO_ALL:
        sendAll(response);
        break;
      case TO_ALL_EXCEPT_SELF:
        final String channelIdStr = (String) request.getBody().get("channel_id");
        final UUID channelId = UUID.fromString(channelIdStr);
        sendAll(response, channelId, incoming);
        break;
      case TO_CHANNEL_EXCEPT_SELF:
        sendAll(response, incoming);
        break;
      case TO_ONE:
        // Reply-to a user type of payload
        // TODO: Get the recipient
        break;
      case TO_SELF:
      default:
        send(incoming, response);
        break;
    }
  }

  /**
   * Method for handling additional leave logic when a client leaves from a server's channel
   *
   * @param incoming client
   * @param request  request payload
   */
  private void handleUsersOnLeave(final Channel incoming, final Payload request) {
    final UUID channelId = UUID.fromString((String) request.getBody().get("channel_id"));

    final Queue<ConnectedClient> joinedChannels = server.getChannelJoinedChannels()
      .computeIfAbsent(channelId, x -> new ArrayBlockingQueue<>(100));

    // Removing the user from the channel when its there
    joinedChannels.removeIf(connectedClient -> connectedClient.getChannel().equals(incoming));
  }

  /**
   * Method for handling additional join logic when a client connects to a channel
   *
   * @param incoming client
   * @param request  request payload
   * @param response current response payload
   * @throws JsonProcessingException exception when JSON processing fails
   */
  private void handleUsersOnJoin(final Channel incoming, final Payload request, final Payload response) throws JsonProcessingException {
    final String channelIdStr = (String) request.getBody().get("channel_id");
    final String jwt = request.getJwt();

    if (jwt == null || channelIdStr == null) return;

    final UUID channelId = UUID.fromString(channelIdStr);
    final DecodedJWT decoded = Auth.decode(jwt);

    final String username = decoded.getSubject();
    final User user = UserTransactions.getUserByUsername(dbManager, username);

    if (user == null) return;

    // Currently connected clients
    final Queue<ConnectedClient> connectedClients = server.getChannelJoinedChannels()
      .computeIfAbsent(channelId, x -> new ArrayBlockingQueue<>(100));

    // Adding the new client to the connected clients
    connectedClients.add(new ConnectedClient(user.getId(), incoming));

    // TODO: Fix later
    final List<String> users = connectedClients.stream()
      .map(connectedClient -> ConvertUser.convertFromConnectedClient(modelMapper, connectedClient, dbManager))
      .filter(Objects::nonNull)
      .map(userDto -> {
        try {
          return userDto.toJson(objectMapper);
        } catch (final JsonProcessingException e) {
          return "";
        }
      })
      .collect(Collectors.toList());

    response.putBody("users", users);

    // Notifying every connected client that another one has connected
    final Payload userJoinedPayload = new Payload()
      .setType(INFO_USER_JOINED_CHANNEL)
      .setResponseType(TO_ALL_EXCEPT_SELF)
      .putBody("user", ConvertUser.convertFromUser(modelMapper, user).toJson(objectMapper));

    for (final ConnectedClient connectedClient : connectedClients)
      if (!connectedClient.getChannel().equals(incoming))
        send(connectedClient.getChannel(), userJoinedPayload);

    // Clearing the jwt from the response (was only used for tracking the joined user)
    request.setJwt(null);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
    // TODO: What to do with the caught exception?
    logger.error("Exception has been caught", cause);
//    ctx.close(); // TODO: Decide what to do when exception is caught. Maybe notify user of server fatal error (500)?
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
      .putBody(BODY_MESSAGE, BODY_INVALID)
      .setResponseType(TO_SELF);
  }

  /**
   * Method for sending payload to channel
   *
   * @param channel channel to send to
   * @param payload payload to send
   */
  public void send(final Channel channel, final Payload payload) {
    try {
      channel.writeAndFlush(payload.toJson(new ObjectMapper()) + "\r\n");
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
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

  public void sendAll(final Payload payload, final UUID channelId, final Channel ignore) {
    final Queue<ConnectedClient> connectedClients = server.getChannelJoinedChannels().get(channelId);
    if (connectedClients == null) return;

    for (final ConnectedClient connectedClient : connectedClients) {
      final Channel channel = connectedClient.getChannel();

      if (channel.equals(ignore)) continue;

      send(channel, payload);
    }
  }

  public void sendToServer(final Payload payload, final UUID serverId) {

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
    if (handlers.containsKey(type))
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
    if (!handlers.containsKey(type))
      throw new InvalidParameterException(String.format("Handler with type %s is not registered", type));

    handlers.remove(type);
  }
}
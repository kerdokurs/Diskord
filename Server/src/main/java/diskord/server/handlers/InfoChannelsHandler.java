package diskord.server.handlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.payload.Payload;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.ChannelTransactions;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.dto.ConvertChannel;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.INFO_CHANNELS_ERROR;
import static diskord.payload.PayloadType.INFO_CHANNELS_OK;
import static diskord.payload.ResponseType.TO_SELF;

public class InfoChannelsHandler extends Handler {
  private final ModelMapper modelMapper = new ModelMapper();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public InfoChannelsHandler(DatabaseManager dbManager, ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  /**
   * Method which each handler must override that handles a specific type of request.
   * For example: LOGIN, REGISTER or MSG.
   *
   * @param request incoming request with server UUID in jwt
   * @param channel
   * @return Response to request, Key "channels" holds a value of List holding all Channels that belong to the server.
   */
  @Override
  public Payload handleRequest(Payload request, Channel channel) {
    Payload response = new Payload();
    response.setResponseTo(request.getId());
    response.setResponseType(TO_SELF);

    try {
      final String token = request.getJwt();
      if (token == null) {
        return response
          .setType(INFO_CHANNELS_ERROR)
          .putBody(BODY_MESSAGE, "Token is does not exist");
      }

      final String serverId = (String) request.getBody().get("server_id");
      if (serverId == null) {
        return response
          .setType(INFO_CHANNELS_ERROR)
          .putBody(BODY_MESSAGE, "Please provide a server id");
      }

      final DecodedJWT decoded = Auth.decode(token);
      final String username = decoded.getSubject();
      final User user = UserTransactions.getUserByUsername(dbManager, username);

      if (user == null) {
        return response
          .setType(INFO_CHANNELS_ERROR)
          .putBody(BODY_MESSAGE, "Invalid account");
      }

      final List<diskord.server.database.channel.Channel> channels = ChannelTransactions.getChannelsByRoomId(dbManager, UUID.fromString(serverId));

      // TODO: Cleanup
      response
        .putBody("channels", channels.stream().map(c -> {
            try {
              return ConvertChannel.convert(modelMapper, c).toJson(objectMapper);
            } catch (JsonProcessingException e) {
              e.printStackTrace();
            }
            return "";
          }).collect(Collectors.toList())
        );
    } catch (JWTVerificationException err) {
      return response
        .setType(INFO_CHANNELS_ERROR)
        .putBody(BODY_MESSAGE, "Decoding the jwt sent by client failed.");
    }

    return response.setType(INFO_CHANNELS_OK);
  }
}

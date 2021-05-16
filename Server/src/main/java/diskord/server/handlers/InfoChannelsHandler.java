package diskord.server.handlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.ChannelTransactions;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import java.util.List;
import java.util.UUID;

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadBody.SERVER_ID;
import static diskord.payload.PayloadType.INFO_CHANNELS_ERROR;
import static diskord.payload.PayloadType.INFO_CHANNELS_OK;
import static diskord.payload.ResponseType.TO_SELF;

public class InfoChannelsHandler extends Handler{

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

    try{
      List<diskord.server.database.channel.Channel> channelsByRoomId;
      try{
        channelsByRoomId =
          ChannelTransactions.getChannelsByRoomId(dbManager, (UUID) request.getBody().get(SERVER_ID));
      } catch (Exception e){
        return response
          .setType(INFO_CHANNELS_ERROR)
          .putBody(BODY_MESSAGE, "Error receiving channels from db.");
      }

      response.putBody("channels", channelsByRoomId);
    } catch(JWTVerificationException err){
      return response
        .setType(INFO_CHANNELS_ERROR)
        .putBody(BODY_MESSAGE, "Decoding the jwt sent by client failed.");
    }
    return response.setType(INFO_CHANNELS_OK);
  }
}

package diskord.server.handlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import diskord.server.crypto.Auth;
import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.UserTransactions;
import diskord.server.database.user.User;
import diskord.server.init.ServerHandler;
import io.netty.channel.Channel;

import java.util.Set;
import java.util.UUID;

import static diskord.payload.PayloadBody.*;
import static diskord.payload.PayloadType.JOIN_SERVER_ERROR;

public class JoinServerHandler extends Handler{
  public JoinServerHandler(DatabaseManager dbManager, ServerHandler serverHandler) {
    super(dbManager, serverHandler);
  }

  /**
   * Method which each handler must override that handles a specific type of request.
   * For example: LOGIN, REGISTER or MSG.
   *
   * @param request incoming request
   * @param channel
   * @return response to that request
   */
  @Override
  public Payload handleRequest(Payload request, Channel channel) {
    Payload response = new Payload();
    request.setResponseTo(request.getId());
    //<server_id, jwt token>
    try{
      DecodedJWT decoded = Auth.decode(request.getJwt());
      final User user = UserTransactions.getUserByUsername(dbManager, decoded.getSubject());
      Set<UUID> joinedServers;
      try{
        joinedServers = user.getJoinedServers();
      } catch (Exception err){
        return request.setType(JOIN_SERVER_ERROR)
                      .putBody(BODY_FIELD, "server")
                      .putBody(BODY_MESSAGE, "Receiving Set<> of Users joined servers failed.");
      }

      UUID serverId;
      try{
        serverId = UUID.fromString((String) request.getBody().get(SERVER_ID));
        //TODO: check UUID validity by trying to find corresponding server? (inefficient/pointless?)
      } catch (Exception err){
        return request.setType(JOIN_SERVER_ERROR)
                      .putBody(BODY_FIELD, "server")
                      .putBody(BODY_MESSAGE, "Invalid UUID received from server.");
      }

      try{
        joinedServers.add(UUID.fromString((String) request.getBody().get(SERVER_ID)));
      } catch (IllegalArgumentException err){
        return request.setType(JOIN_SERVER_ERROR)
                      .putBody(BODY_FIELD, "server")
                      .putBody(BODY_MESSAGE, "Updating Users joined server Set<> failed.");
      }

    } catch (JWTVerificationException err){
      response
        .setResponseTo(request.getId())
        .putBody(BODY_FIELD, "server")
        .putBody(BODY_MESSAGE, "Decoding the jwt token received from server failed.");
      //error handle
    }

    return response;
  }
}

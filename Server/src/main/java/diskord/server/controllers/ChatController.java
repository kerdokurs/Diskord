package diskord.server.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.server.database.DatabaseManager;
import diskord.server.database.attachment.Attachment;
import diskord.server.database.message.Message;
import diskord.server.database.room.Room;
import diskord.server.database.user.User;

import javax.validation.constraints.NotNull;
import java.util.Base64;
import java.util.UUID;

import static diskord.payload.PayloadBody.BODY_MESSAGE;
import static diskord.payload.PayloadType.*;

public class ChatController {
  private ChatController() {
  }

  public static Payload handleMessage(@NotNull final DatabaseManager dbManager, @NotNull final Payload request) {
    final Payload response = new Payload();

    final PayloadBody body = request.getBody();

    final String content = (String) body.get("message");
    final String attachmentData = (String) body.get("attachment");
    final String roomId = (String) body.get("room");

    final String jwt = request.getJwt();
    final DecodedJWT token = JWT.decode(jwt);

    if (token == null) {
      // Something was wrong with decoding the jwt
      return response
        .setType(AUTH_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "token not valid");
    }

    // TODO: Validate the message[0<length<=255 constraint implemented, might want to add more constraints later tho]

    if (content.length() == 0) {
      return response
        .setType(CHAT_ERROR_EMPTY)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "Message cannot be empty.");
    }
    if(content.length() > 255){
      return response
        .setType(CHAT_ERROR_TOOLONG)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "Message cannot be over 255 characters long.");
    }

    // We can get the author from the jwt like this
    final UUID authorId = UUID.fromString(token.getSubject());
    final User author = dbManager.getOne(User.class, authorId);

    if (author == null) {
      return response
        .setType(AUTH_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "invalid author");
    }

    final UUID roomUuid = UUID.fromString(roomId);
    final Room room = dbManager.getOne(Room.class, roomUuid);

    if (room == null) {
      return response
        .setType(CHAT_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "invalid room");
    }

    final Attachment attachment = new Attachment()
      .setType("image/png")
      .setName("test.png");

    try{
      Base64.Decoder decoder = Base64.getDecoder();
      decoder.decode(attachmentData);
      attachment.setBase64(attachmentData);
    } catch(IllegalArgumentException e){
      return response
        .setType(CHAT_ERROR_INVALID_ATTACHMENT)
        .putBody(BODY_MESSAGE, "Decoding string to base64 failed. Invalid attachment.");
    }
    final Message message = new Message()
      .setRoom(room)
      .setContent(content)
      .setAttachment(attachment)
      .setAuthor(author);

    dbManager.save(attachment);
    dbManager.save(message);

    return response
      .setType(CHAT_OK)
      .setResponseTo(request.getId());
  }
}

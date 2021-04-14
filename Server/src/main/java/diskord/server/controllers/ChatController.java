package diskord.server.controllers;

import diskord.server.crypto.JWT;
import diskord.server.database.DatabaseManager;
import diskord.server.database.attachment.Attachment;
import diskord.server.database.message.Message;
import diskord.server.database.room.Room;
import diskord.server.database.user.User;
import diskord.server.payload.Payload;
import diskord.server.payload.PayloadBody;
import io.jsonwebtoken.Claims;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static diskord.server.payload.PayloadBody.BODY_MESSAGE;
import static diskord.server.payload.PayloadType.*;

public class ChatController {
  public static Payload handleMessage(@NotNull final Payload request) {
    final Payload response = new Payload();

    final PayloadBody body = request.getBody();
    final String content = (String) body.get("message");
    final String attachmentData = (String) body.get("attachment");
    final String roomId = (String) body.get("room");
    final String jwt = request.getJwt();

    final Claims validatedJWT = JWT.validate(jwt); // check use validity

    if (validatedJWT == null) {
      // Something was wrong with decoding the jwt
      return response
        .setType(AUTH_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "token not valid");
    }

    // TODO: Validate the message
    // suppose this is the validation for now.
    if (content.length() == 0) {
      return response
        .setType(CHAT_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "invalid message");
    }

    // We can get the author id from the jwt like this
    final UUID authorId = UUID.fromString(validatedJWT.getSubject());
    final User author = DatabaseManager.userRepository().findOne(authorId);

    if (author == null) {
      return response
        .setType(AUTH_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "invalid author");
    }

    final UUID roomUuid = UUID.fromString(roomId);
    final Room room = DatabaseManager.roomRepository().findOne(roomUuid);

    if (room == null) {
      return response
        .setType(CHAT_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "invalid room");
    }

    // TODO: check the validity of the attachment base64 string

    final Attachment attachment = new Attachment()
      .setType("image/png")
      .setName("test.png")
      .setBase64(attachmentData);

    final Message message = new Message()
      .setRoom(room)
      .setContent(content)
      .setAttachment(attachment)
      .setAuthor(author);

    DatabaseManager.attachmentRepository().save(attachment);
    DatabaseManager.messageRepository().save(message);

    return response
      .setType(CHAT_OK)
      .setResponseTo(request.getId());
  }
}

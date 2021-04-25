package diskord.server.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diskord.payload.Payload;
import diskord.payload.PayloadBody;
import diskord.server.database.DatabaseManager;
import diskord.server.database.attachment.Attachment;
import diskord.server.database.channel.Channel;
import diskord.server.database.message.Message;
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
    final String channelId = (String) body.get("channel_id");

    final String attachmentData = (String) body.get("attachment");
    final String attachmentType = (String) body.get("attachment_type");
    final String attachmentName = (String) body.get("attachment_name");

    final String jwt = request.getJwt();
    final DecodedJWT token;

    try {
      token = JWT.decode(jwt);

      if (token == null) {
        // Something was wrong with decoding the jwt
        return response
          .setType(AUTH_ERROR)
          .setResponseTo(request.getId())
          .putBody(BODY_MESSAGE, "token not valid");
      }
    } catch (final JWTDecodeException e) {
      return response
        .setType(AUTH_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "token not valid");
    }

    // TODO: Check if user is allowed to send message

    // TODO: Validate the message
    // suppose this is the validation for now.
    if (content.length() == 0) {
      return response
        .setType(MSG_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "invalid message");
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

    final UUID channelUuid = UUID.fromString(channelId);
    final Channel channel = dbManager.getOne(Channel.class, channelUuid);

    if (channel == null) {
      return response
        .setType(MSG_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "invalid channel");
    }

    try {
      Base64.getDecoder().decode(attachmentData);
    } catch (final IllegalArgumentException e) {
      return response
        .setType(ATTACHMENT_ERROR)
        .setResponseTo(request.getId())
        .putBody(BODY_MESSAGE, "attachment not valid");
    }

    final Attachment attachment = new Attachment()
      .setType(attachmentType)
      .setName(attachmentName)
      .setBase64(attachmentData);

    final Message message = new Message()
      .setChannel(channel)
      .setContent(content)
      .setAttachment(attachment)
      .setAuthor(author);

    dbManager.save(attachment);
    dbManager.save(message);

    return response
      .setType(MSG_OK)
      .setResponseTo(request.getId());
  }
}

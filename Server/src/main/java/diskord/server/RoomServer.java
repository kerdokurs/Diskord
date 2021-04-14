package diskord.server;

import diskord.server.crypto.JWT;
import diskord.server.database.DatabaseManager;
import diskord.server.database.message.Message;
import diskord.server.database.room.Room;
import diskord.server.database.user.User;
import diskord.server.payload.Payload;
import diskord.server.payload.PayloadBody;
import io.jsonwebtoken.Claims;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import static diskord.server.payload.PayloadBody.BODY_MESSAGE;
import static diskord.server.payload.PayloadType.*;

public class RoomServer extends Server {
  // TODO: Should a room server know which room its hosting???

  protected RoomServer(final int port) {
    super(port);
  }

  @Override
  protected void handlePayload(final Payload payload, final SelectionKey key) throws ClosedChannelException {
    final SocketChannel socketChannel = (SocketChannel) key.channel();

    final Payload response;

    switch (payload.getType()) {
      case CHAT:
        final PayloadBody body = payload.getBody();
        final String content = (String) body.get("message");
        final String attachment = (String) body.get("attachment");
        final String roomId = (String) body.get("room");
        final String jwt = payload.getJwt();

        final Claims validatedJWT = JWT.validate(jwt); // check use validity

        if (validatedJWT == null) {
          // Something was wrong with decoding the jwt
          response = new Payload()
            .setType(AUTH_ERROR)
            .setResponseTo(payload.getId())
            .putBody(BODY_MESSAGE, "token not valid");
          break;
        }

        // TODO: Validate the message
        // suppose this is the validation for now.
        if (content.length() == 0) {
          response = new Payload()
            .setType(CHAT_ERROR)
            .setResponseTo(payload.getId())
            .putBody(BODY_MESSAGE, "invalid message");
          break;
        }

        // We can get the author id from the jwt like this
        final UUID authorId = UUID.fromString(validatedJWT.getSubject());
        final User author = DatabaseManager.userRepository().findOne(authorId);

        if (author == null) {
          response = new Payload()
            .setType(AUTH_ERROR)
            .setResponseTo(payload.getId())
            .putBody(BODY_MESSAGE, "invalid author");
          break;
        }

        final UUID roomUuid = UUID.fromString(roomId);
        final Room room = DatabaseManager.roomRepository().findOne(roomUuid);

        if (room == null) {
          response = new Payload()
            .setType(CHAT_ERROR)
            .setResponseTo(payload.getId())
            .putBody(BODY_MESSAGE, "invalid room");
          break;
        }

        // TODO: check the validity of the attachment base64 string

        final Message message = new Message()
          .setRoom(room)
          .setContent(content)
          .setAttachment(attachment)
          .setAuthor(author);

        DatabaseManager.messageRepository().save(message);

        response = new Payload()
          .setType(CHAT_OK)
          .setResponseTo(payload.getId());

        break;
      default:
        response = unhandledPayload(payload, key);
    }

    socketMap.get(socketChannel).add(response);
    socketChannel.register(selector, SelectionKey.OP_WRITE);
  }
}

package diskord.server.channel;

import diskord.server.database.user.Role;
import diskord.server.database.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

/**
 * Hoiame tavalise kasutaja ja andmebaasi kasutaja eraldi.
 * Siis ei teki probleeme. Juhul kui kustutada andmebaasi
 * kasutaja, võib kanal hakata vigu viskama, et kasutaja
 * puudub vms. Samuti võib Hibernate vinguma hakata, et
 * ei saa kasutajat kustutada, kuna on kasutuses vms.
 */
public class ChannelUser {
  @Getter
  private final Queue<String> messageQueue = new ArrayDeque<>();

  @Getter
  @Setter
  private UUID id;

  @Getter
  @Setter
  private String username;

  @Getter
  @Setter
  private Role role;

  public static ChannelUser createChannelUser(final User user) {
    return new ChannelUser().setId(user.getId()).setUsername(user.getUsername()).setRole(user.getRole());
  }

  public void addMessage(final String message) {
    messageQueue.add(message);
  }

  public String getFirstMessage() {
    return !messageQueue.isEmpty() ? null: messageQueue.poll();
  }
}

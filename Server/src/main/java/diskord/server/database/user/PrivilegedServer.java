package diskord.server.database.user;

import diskord.server.database.room.Room;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "joined_servers")
public class PrivilegedServer {
  @Id
  @Getter
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
    name = "UUID",
    strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(
    name = "id",
    unique = true,
    nullable = false,
    updatable = false
  )
  private UUID id;

  @Getter
  private UUID userId;

  @Getter
  private UUID roomId;

  public PrivilegedServer(final User user, final Room room) {
    userId = user.getId();
    roomId = room.getId();
  }

  public PrivilegedServer() {
  }
}

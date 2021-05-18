package diskord.server.database.user;

import diskord.server.database.room.Room;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "joined_servers")
public class JoinedServer {
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
  @OneToOne(fetch = FetchType.EAGER)
  private User user;

  @Getter
  @OneToOne(fetch = FetchType.EAGER)
  private Room room;

  public JoinedServer(final User user, final Room room) {
    this.user = user;
    this.room = room;
  }

  public JoinedServer() {
  }
}

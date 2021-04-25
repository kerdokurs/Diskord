package diskord.server.database.channel;

import diskord.server.database.room.Room;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "channels")
public class Channel {
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
  @Setter
  private String name;

  @ManyToOne
  @JoinColumn(name = "room")
  @Getter
  @Setter
  private Room room;

  @Getter
  private Date createdAt = new Date();

  public Channel() {
  }
}

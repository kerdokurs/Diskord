package diskord.server.database.room;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {
  @Id
  @Getter
  @Setter
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

  @Getter
  @Setter
  private String description;

  @Getter
  @Setter
  @Column(
    columnDefinition = "TEXT"
  )
  private String icon;

  @Getter
  @Setter
  private String joinId;

  public Room() {
  }

  public Room(final String name, final String description, final String icon) {
    this.name = name;
    this.description = description;
    this.icon = icon;

    this.joinId = UUID.randomUUID().toString().substring(0, 6);
  }
}

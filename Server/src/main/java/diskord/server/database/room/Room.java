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
  private String icon;

  //TODO: Implement JoinId to make it easier for users to join a server.
  //Client expects method "getJoinID"
//  @Getter
//  @Setter
//  private String joinId;
}

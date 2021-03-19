package diskord.server.jpa.channel;

import diskord.server.jpa.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
  @Column(
      name = "name",
      nullable = false,
      unique = true
  )
  @Size(
      min = 6,
      max = 24,
      message = "kanali nimi peab jääma 6 ja 24 tähemärgi vahele"
  )
  @Accessors(
      chain = true
  )
  private String name;

  @Getter
  @Setter
  @OneToOne
  @NotNull
  @Accessors(
      chain = true
  )
  private User owner;

  // private Map<User, Role> userRoles;

  public Channel() {
  }

  public Channel(@NotNull final String name, @NotNull final User owner) {
    this.name = name;
    this.owner = owner;
  }

  @Override
  public String toString() {
    return "Channel{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", owner=" + owner +
        '}';
  }
}

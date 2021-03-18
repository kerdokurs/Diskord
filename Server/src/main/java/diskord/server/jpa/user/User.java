package diskord.server.jpa.user;

import diskord.server.crypto.Hash;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
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
  @Column(
      name = "username",
      unique = true
  )
  @Size(
      min=5,
      message = "kasutajanimi peab olema vähemalt 5-tähemärgi pikkune"
  )
  private String username;

  @Getter
  @Setter
  @NotNull
  private String password;

  @Getter
  @CreationTimestamp
  @Column(
      name = "created_at"
  )
  private Date createdAt;

  @Getter
  @Setter
  @UpdateTimestamp
  @Column(
      name = "updated_at"
  )
  private Date updatedAt;

  public User(final String username, final String password) {
    this.username = username;
    this.password = Hash.hash(password);
  }

  public User() {
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        '}';
  }
}

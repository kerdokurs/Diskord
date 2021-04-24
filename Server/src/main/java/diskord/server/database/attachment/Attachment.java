package diskord.server.database.attachment;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "attachments")
public class Attachment {
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
  private String base64;

  @Getter
  @Setter
  private String type;

  @Getter
  @Setter
  private String name;

  @Getter
  private Date timestamp;

  public Attachment() {
    timestamp = new Date();
  }

  @Override
  public String toString() {
    return "Attachment{" +
      "id=" + id +
      ", base64='" + base64.substring(0, Math.min(base64.length(), 100)) + '\'' +
      ", type='" + type + '\'' +
      ", name='" + name + '\'' +
      ", timestamp=" + timestamp +
      '}';
  }
}

package diskord.server.database.message;

import diskord.server.database.attachment.Attachment;
import diskord.server.database.room.Room;
import diskord.server.database.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message {
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
  private String content;

  @Getter
  @Setter
  @OneToOne
  @JoinColumn(name = "attachment")
  private Attachment attachment;

  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "room")
  private Room room;

  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "author")
  private User author;

  @Getter
  private Date timestamp;

  /**
   * Salvestame ainult mingi 200-500 viimast sõnumit
   */
  public Message() {
    timestamp = new Date();
  }

  @Override
  public String toString() {
    return "Message{" +
      "id=" + id +
      ", content='" + content + '\'' +
      ", attachment='" + attachment + '\'' +
      ", room=" + room +
      ", author=" + author +
      ", timestamp=" + timestamp +
      '}';
  }
}
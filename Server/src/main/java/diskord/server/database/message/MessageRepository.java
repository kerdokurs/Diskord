package diskord.server.database.message;

import diskord.server.database.DatabaseManager;
import diskord.server.database.Repository;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class MessageRepository extends Repository<Message, UUID> {
  public MessageRepository() {
    super(Message.class);
  }

  public List<Message> getRoomMessages(@NotNull final UUID roomId) {
    final String query = "SELECT m FROM Message WHERE m.room.id = :roomId";
    final TypedQuery<Message> tq = DatabaseManager.entityManager().createQuery(query, Message.class);
    tq.setParameter("roomId", roomId);

    return tq.getResultList();
  }

  public List<Message> getRoomMessages(@NotNull final UUID roomId, final int start, final int amt) {
    final String query = "SELECT m FROM Message WHERE m.room.id = :roomId ORDER BY m.timestamp DESC";
    final TypedQuery<Message> tq = DatabaseManager.entityManager()
      .createQuery(query, Message.class)
      .setMaxResults(amt)
      .setFirstResult(start);
    tq.setParameter("roomId", roomId);

    return tq.getResultList();
  }

  @Override
  public List<Message> getAll() {
    final String query = "SELECT m FROM Message";
    final TypedQuery<Message> tq = DatabaseManager.entityManager().createQuery(query, Message.class);

    return tq.getResultList();
  }
}

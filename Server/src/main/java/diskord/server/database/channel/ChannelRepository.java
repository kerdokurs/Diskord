package diskord.server.database.channel;

import diskord.server.database.Repository;
import javassist.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class ChannelRepository implements Repository<Channel, UUID> {
  public static ChannelRepository INSTANCE;

  private final EntityManager em;

  public ChannelRepository(final EntityManager em) {
    this.em = em;
  }

  @Override
  public Channel findOne(final @NotNull UUID id) {
    final Channel channel = em.find(Channel.class, id);

    return channel;
  }

  @Override
  public List<Channel> findAll() {
    final String query = "SELECT c FROM Channel c WHERE c.id IS NOT NULL";

    final TypedQuery<Channel> tq = em.createQuery(query, Channel.class);

    return tq.getResultList();
  }

  @Override
  public boolean save(@NotNull final Channel channel) {
    EntityTransaction et = null;

    try {
      et = em.getTransaction();

      et.begin();

      em.persist(channel);
      em.flush();

      et.commit();
    } catch (final Exception e) {
      if (et != null) et.rollback();

      return false;
    }

    return true;
  }

  @Override
  public boolean delete(@NotNull final UUID uuid) throws NotFoundException {
    return false;
  }
}

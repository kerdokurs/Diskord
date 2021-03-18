package diskord.server.jpa.user;

import diskord.server.Repository;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

public class UserRepository implements Repository<User, UUID> {
  private final EntityManagerFactory factory;

  public UserRepository(final EntityManagerFactory factory) {
    this.factory = factory;
  }

  /**
   * Tagastab andmebaasist kasutaja id-ga id
   *
   * @param id kasutaja id
   * @return kasutaja
   */
  @Override
  public User findOne(final UUID id) {
    final EntityManager em = factory.createEntityManager();
    final User user = em.find(User.class, id);
    em.close();

    return user;
  }

  public User findOne(final String username) {
    final EntityManager em = factory.createEntityManager();
    final String query = "SELECT u FROM User u WHERE u.username = :username";
    final TypedQuery<User> tq = em.createQuery(query, User.class);
    tq.setParameter("username", username);

    User user;
    try {
      user = tq.getSingleResult();
    } finally {
      em.close();
    }

    return user;
  }

  /**
   * Tagastab kõik kasutajad andmebaasist.
   *
   * @return kasutajad
   */
  @Override
  public List<User> findAll() throws NoResultException {
    final EntityManager em = factory.createEntityManager();
    final String query = "SELECT u FROM User u WHERE u.id IS NOT NULL";

    final TypedQuery<User> tq = em.createQuery(query, User.class);

    List<User> users;

    try {
      users = tq.getResultList();
    } finally {
      em.close();
    }

    return users;
  }

  /**
   * Kirjutab uue kasutaja andmebaasi
   *
   * @param user kasutaja
   * @return <code>true</code>, kui salvestamine õnnestus, <code>false</code>, kui ebaõnnestus
   */
  @Override
  public boolean save(final User user) {
    final EntityManager em = factory.createEntityManager();
    EntityTransaction et = null;

    try {
      et = em.getTransaction();

      et.begin();

      em.persist(user);
      em.flush();

      et.commit();
    } catch (final Exception e) {
      // TODO: Pane siia täpsem Exception
      if (et != null) et.rollback();

      return false;
    } finally {
      em.close();
    }

    return true;
  }
}

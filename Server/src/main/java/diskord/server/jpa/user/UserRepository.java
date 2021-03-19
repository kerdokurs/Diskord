package diskord.server.jpa.user;

import diskord.server.jpa.Repository;
import javassist.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class UserRepository implements Repository<User, UUID> {
  private final EntityManagerFactory factory;

  public UserRepository(final EntityManagerFactory factory) {
    this.factory = factory;
  }

  /**
   * Tagastab andmebaasist otsitava kasutaja
   *
   * @param id otsitava kasutaja id
   * @return otsitav kasutaja
   */
  @Override
  public User findOne(@NotNull final UUID id) {
    final EntityManager em = factory.createEntityManager();
    final User user = em.find(User.class, id);
    em.close();

    return user;
  }

  /**
   * Tagastab otsitava kasutaja
   *
   * @param username kasutaja kasutajanimi
   * @return otsitav kasutaja
   */
  public User findOne(@NotNull final String username) {
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
   * Tagastab kõik kasutajad
   *
   * @return kõik kasutajad
   */
  @Override
  public List<User> findAll() {
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
   * Salvestab kasutaja andmebaasi
   *
   * @param user salvestatav kasutaja
   * @return <code>true</code>, kui salvestamine õnnestus, <code>false</code> muul juhul
   */
  @Override
  public boolean save(@NotNull final User user) {
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

  /**
   * Kustutab kasutaja
   *
   * @param id kasutaja id
   * @return <code>true</code>, kui kustutamine õnnestus, <code>false</code> muul juhul
   * @throws NotFoundException kui kasutajat ei leitud
   */
  @Override
  public boolean delete(@NotNull final UUID id) throws NotFoundException {
    final EntityManager em = factory.createEntityManager();

    final User user = findOne(id);

    if (user == null) throw new NotFoundException("kasutajat ei leitud");

    try {
      em.detach(user); // mdea, kas on vaja või üldse töötab nii
      em.remove(user);
      em.flush();
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      em.close();
    }

    return true;
  }
}

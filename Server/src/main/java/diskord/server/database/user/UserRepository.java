package diskord.server.database.user;

import diskord.server.database.Repository;
import javassist.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository implements Repository<User, UUID> {
  private final EntityManager em;

  public UserRepository(final EntityManager em) {
    this.em = em;
  }

  /**
   * Tagastab andmebaasist otsitava kasutaja<br>
   *
   * Kui ei leia siis tagastab <i>null</i>-i
   * @param id otsitava kasutaja id
   * @return otsitav kasutaja
   */
  @Override
  public User findOne(@NotNull final UUID id) {
    try{
      return em.find(User.class, id);
    } catch (IllegalArgumentException e){
      System.out.println("Can not find the specified user from the database.");
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Tagastab otsitava kasutaja
   * Kui otsitavat kasutajat ei leidu siis püüab NoResultExceptioni, prindib stack trace ning
   * tagastab <i>null</i>-i
   * @param username kasutaja kasutajanimi
   * @return otsitav kasutaja
   */
  public User findOne(@NotNull final String username) {
    try{
      final String query = "SELECT u FROM User u WHERE u.username = :username";
      final TypedQuery<User> tq = em.createQuery(query, User.class);
      tq.setParameter("username", username);

      return tq.getSingleResult();
    } catch (NoResultException e){
      System.out.println("Can not find the specified user!");
      e.printStackTrace();
    }
    return null;
  }


  /**
   * Tagastab kõik kasutajad
   *
   * @return kõik kasutajad,
   */
  @Override
  public List<User> findAll() {
    final String query = "SELECT u FROM User u WHERE u.id IS NOT NULL";
    final TypedQuery<User> tq = em.createQuery(query, User.class);

    try{
      return tq.getResultList();

    } catch (Exception e){
      e.printStackTrace();
      return new ArrayList<>(); //return an empty ArrayList<User> in case of an error.
    }

  }

  /**
   * Salvestab kasutaja andmebaasi
   *
   * @param user salvestatav kasutaja
   * @return <code>true</code>, kui salvestamine õnnestus, <code>false</code> muul juhul
   */
  @Override
  public boolean save(@NotNull final User user) {
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
    final User user = findOne(id);

    if (user == null) throw new NotFoundException("kasutajat ei leitud");

    try {
      em.detach(user); // mdea, kas on vaja või üldse töötab nii
      em.remove(user);
      em.flush();
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }
}

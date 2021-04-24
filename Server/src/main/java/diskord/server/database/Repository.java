package diskord.server.database;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.validation.constraints.NotNull;
import java.util.List;

public abstract class Repository<T, ID> {
  private final Class<T> entityClass;

  protected Repository(final Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  public T getOne(@NotNull ID id) {
//    final EntityManager em = DatabaseManager.entityManager();
//    return em.find(entityClass, id);
    return null;
  }

  // TODO: Find a way to do this generically.
  public abstract List<T> getAll();

  public boolean save(@NotNull T obj) {
    EntityTransaction et = null;

//    final EntityManager em = DatabaseManager.entityManager();
    final EntityManager em = null;

    try {
      et = em.getTransaction();

      et.begin();

      em.persist(obj);

      et.commit();
    } catch (final EntityExistsException e) {
      if (et != null) et.rollback();

      return false;
    }

    return true;
  }

  public boolean deleteById(@NotNull ID id) {
    final T obj = getOne(id);
    return delete(obj);
  }

  public boolean delete(@NotNull T obj) {
    // WARNING: Not testable
//    final EntityManager em = DatabaseManager.entityManager();
  final EntityManager em = null;

    try {
      // idk if necessary
      em.detach(obj);
      em.remove(obj);
    } catch (final Exception e) {
      // TODO: Add more specific exception
      // TODO: Should it just throw the exception?
      return false;
    }

    return false;
  }
}

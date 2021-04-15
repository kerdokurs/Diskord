package diskord.server.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DatabaseManager {
  private final EntityManagerFactory factory;

  public DatabaseManager() {
    factory = Persistence.createEntityManagerFactory("DiskordServer.database");
  }

  public <T, D> T getOne(@NotNull Class<T> entityClass, @NotNull D primaryKey) {
    return runTransaction(em -> em.find(entityClass, primaryKey));
  }

  public <T> List<T> getAll(Class<T> entityClass) {
    return runTransaction(em ->
      em.createQuery(String.format("FROM %s", entityClass.getName()), entityClass)
        .getResultList()
    );
  }

  public <T> boolean save(@NotNull T obj) {
    return runTransaction(em -> {
      EntityTransaction et = em.getTransaction();

      try {
        et.begin();
        em.persist(obj);
        et.commit();
      } catch (Exception e) {
        if (et != null) et.rollback();

        return false;
      }

      return true;
    });
  }

  public <T> boolean delete(@NotNull T obj) {
    return runTransaction(em -> delete(obj, em));
  }

  public <T> boolean delete(@NotNull T obj, EntityManager em) {
    em.remove(obj);
    return true;
  }

  public <T, D> boolean deleteById(@NotNull Class<T> entityClass, @NotNull D id) {
    return runTransaction(em -> {
      final T obj = getOne(entityClass, id);
      return delete(obj, em);
    });
  }

  public <T> T runTransaction(final Transaction<T> transaction) {
    final EntityManager em = factory.createEntityManager();

    final T t = transaction.execute(em);

    em.close();

    return t;
  }
}

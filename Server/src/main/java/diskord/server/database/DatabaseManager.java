package diskord.server.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class DatabaseManager {
  private final EntityManagerFactory factory;

  public DatabaseManager() {
    factory = Persistence.createEntityManagerFactory("DiskordServer.database");
    System.out.println("db manager has been initialized");
  }

  public void close() {
    factory.close();
  }

  public <T, D> T getOne(final Class<T> entityClass, final D primaryKey) {
    return runTransaction(em -> em.find(entityClass, primaryKey));
  }

  /**
   * Method for getting all entities by specified class.<p>
   * <b>PS</b>. The table of the entity in the database must match its classname.
   *
   * @param entityClass class of entity
   * @param <T>         type of entity
   * @return list of entities
   */
  public <T> List<T> getAll(final Class<T> entityClass) {
    return getAll(entityClass, entityClass.getSimpleName());
  }

  /**
   * Method for getting all entities by specified class from specified table name.
   * If table name matches the class name of the entity, DatabaseManager#getAll(Class)
   * should be used.
   *
   * @param entityClass class of entity
   * @param tableName   database table name of entity
   * @param <T>         type of entity
   * @return list of entities
   * @see DatabaseManager#getAll(Class)
   */
  public <T> List<T> getAll(final Class<T> entityClass, final String tableName) {
    return runTransaction(em ->
      em.createQuery(String.format("FROM %s", tableName), entityClass)
        .getResultList()
    );
  }

  /**
   * Method for saving an entity.
   *
   * @param obj entity to save
   * @param <T> type of entity
   * @return boolean whether the entity was saved
   */
  public <T> boolean save(final T obj) {
    return runTransaction(em -> {
      final EntityTransaction et = em.getTransaction();

      try {
        et.begin();
        em.persist(obj);
        et.commit();
      } catch (final Exception e) {
        et.rollback();

        return false;
      }

      return true;
    });
  }

  /**
   * Method for deleting an entity by its reference.
   *
   * @param obj entity to delete
   * @param <T> type of entity
   * @return boolean whether the entity was deleted
   */
  public <T> boolean delete(final T obj) {
    return runTransaction(em -> delete(obj, em));
  }

  /**
   * Method for deleting entity by its reference. Only used inside database manager since
   * is requires an entity manager as the second argument which is conveniently generated
   * in DatabaseManager#runTransaction.
   *
   * @param obj entity to delete
   * @param em  entity manager
   * @param <T> type of entity
   * @return boolean whether the entity was deleted
   */
  private <T> boolean delete(final T obj, final EntityManager em) {
    final EntityTransaction et = em.getTransaction();

    try {
      et.begin();
      em.remove(obj);
      et.commit();
    } catch (final Exception e) {
      et.rollback();

      return false;
    }

    return true;
  }

  /**
   * Method for deleting entity with provided id.
   *
   * @param entityClass class of the entity to delete
   * @param id          entity id
   * @param <T>         type of entity
   * @param <D>         type of entity primary key
   * @return boolean whether the entity was deleted
   */
  public <T, D> boolean deleteById(final Class<T> entityClass, final D id) {
    return runTransaction(em -> {
      final T obj = getOne(entityClass, id);
      return delete(obj, em);
    });
  }

  /**
   * Method for running database transactions. Entity manager will be passed to the transaction
   * as the only argument for easy use and will be closed after the transaction has been completed.
   *
   * @param transaction transaction to run
   * @param <T>         type to run the transaction on
   * @return result of the transaction
   */
  public <T> T runTransaction(final Transaction<T> transaction) {
    final EntityManager em = factory.createEntityManager();

    final T t = transaction.execute(em);

    em.close();

    return t;
  }
}

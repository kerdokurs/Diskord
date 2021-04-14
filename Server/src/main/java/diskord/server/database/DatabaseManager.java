package diskord.server.database;

import diskord.server.database.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseManager {
  private static EntityManager entityManager;

  private static UserRepository userRepository;

  public static EntityManager entityManager() {
    if (entityManager == null) {
      final EntityManagerFactory factory = Persistence.createEntityManagerFactory("DiskordServer.database");
      entityManager = factory.createEntityManager();
    }

    return entityManager;
  }

  public static UserRepository userRepository() {
    if (userRepository == null)
      userRepository = new UserRepository(entityManager());

    return userRepository;
  }
}

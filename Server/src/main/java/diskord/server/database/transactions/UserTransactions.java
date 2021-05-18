package diskord.server.database.transactions;

import diskord.server.database.DatabaseManager;
import diskord.server.database.user.User;

public class UserTransactions {
  private UserTransactions() {
  }

  public static User getUserByUsername(final DatabaseManager dbManager, final String username) {
    return dbManager.runTransaction(em ->
      em.createQuery("FROM User u WHERE u.username = :username", User.class)
        .setParameter("username", username)
        .getSingleResult()
    );
  }

  public static boolean doesUserExist(final DatabaseManager dbManager, final String username) {
    return dbManager.runTransaction(em ->
      (Long) em.createQuery("SELECT COUNT(*) FROM User u WHERE u.username = :username").setParameter("username", username).getSingleResult() > 0
    );
  }
}

package diskord.server.database.transactions;

import diskord.server.database.DatabaseManager;
import diskord.server.database.user.User;

public class UserTransactions {
  private UserTransactions() {
  }

  public static User getUserByUsername(DatabaseManager dbManager, final String username) {
    return dbManager.runTransaction(em ->
      em.createQuery("FROM User u WHERE u.username = :username", User.class)
        .setParameter("username", username)
        .getSingleResult()
    );
  }
}

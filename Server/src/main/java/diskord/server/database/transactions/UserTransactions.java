package diskord.server.database.transactions;

import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;
import diskord.server.database.user.JoinedServer;
import diskord.server.database.user.PrivilegedServer;
import diskord.server.database.user.User;

import java.util.List;
import java.util.UUID;

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

  public static List<JoinedServer> getUserJoinedRooms(final DatabaseManager dbManager, final UUID userId) {
    return dbManager.runTransaction(em ->
      em.createQuery("FROM JoinedServer jr WHERE jr.userId = :userId", JoinedServer.class)
        .setParameter("userId", userId)
        .getResultList()
    );
  }

  public static List<PrivilegedServer> getUserPrivilegedRooms(final DatabaseManager dbManager, final UUID userId) {
    return dbManager.runTransaction(em ->
      em.createQuery("FROM PrivilegedServer jr WHERE jr.userId = :userId", PrivilegedServer.class)
        .setParameter("userId", userId)
        .getResultList()
    );
  }

  public static boolean addUserJoinedServer(final DatabaseManager dbManager, final User user, final Room room) {
    final JoinedServer server = new JoinedServer(user, room);
    return dbManager.save(server);
  }

  public static boolean addUserPrivilegedServer(final DatabaseManager dbManager, final User user, final Room room) {
    final PrivilegedServer server = new PrivilegedServer(user, room);
    return dbManager.save(server);
  }

  public static boolean doesUserExist(final DatabaseManager dbManager, final String username) {
    return dbManager.runTransaction(em ->
      (Long) em.createQuery("SELECT COUNT(*) FROM User u WHERE u.username = :username").setParameter("username", username).getSingleResult() > 0
    );
  }
}

package diskord.server.database.transactions;

import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;
import diskord.server.database.user.JoinedServer;
import diskord.server.database.user.User;

import java.util.List;
import java.util.UUID;

public class RoomTransactions {
  public static List<Room> getRooms(final DatabaseManager dbManager) {
    return dbManager.getAll(Room.class, "Room");
  }

  public static Room getRoomByUUID(final DatabaseManager dbManager, final UUID roomId) {
    return dbManager.runTransaction(em ->
      em.createQuery("FROM Room u WHERE u.id = :roomId", Room.class)
        .setParameter("roomId", roomId)
        .getSingleResult()
    );
  }

  public static boolean doesRoomExistByName(final DatabaseManager dbManager, final String name) {
    return dbManager.runTransaction(em ->
      (Long) em.createQuery("SELECT COUNT(*) FROM Room r WHERE r.name = :name")
      .setParameter("name", name)
      .getSingleResult() > 0
    );
  }

  public static Room getRoomByJoinId(final DatabaseManager dbManager, final String joinId) {
    return dbManager.runTransaction(em -> em.createQuery("FROM Room u WHERE u.joinId = :joinId", Room.class)
      .setParameter("joinId", joinId)
      .getSingleResult()
    );
  }
}

package diskord.server.database.transactions;

import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;
import diskord.server.database.user.User;

import java.util.List;
import java.util.UUID;

public class RoomTransactions {
  public static List<Room> getRooms(final DatabaseManager dbManager) {
    return dbManager.getAll(Room.class, "Room");
  }

  public static Room getRoomByUUID(final DatabaseManager dbManager, final UUID roomId){
    return dbManager.runTransaction(em ->
      em.createQuery("FROM Room u WHERE u.id = :roomId", Room.class)
        .setParameter("roomId", roomId)
        .getSingleResult()
    );
  }
}

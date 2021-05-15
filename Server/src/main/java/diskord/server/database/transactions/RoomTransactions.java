package diskord.server.database.transactions;

import diskord.server.database.DatabaseManager;
import diskord.server.database.room.Room;

import java.util.List;

public class RoomTransactions {
  public static List<Room> getRooms(final DatabaseManager dbManager) {
    return dbManager.getAll(Room.class, "rooms");
  }
}

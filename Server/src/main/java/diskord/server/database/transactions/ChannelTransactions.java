package diskord.server.database.transactions;

import diskord.server.database.DatabaseManager;
import diskord.server.database.channel.Channel;

import java.util.List;
import java.util.UUID;

public class ChannelTransactions {
  public static List<Channel> getChannelsByRoomId(
    final DatabaseManager dbManager,
    final UUID roomId
  ) {
    return dbManager.runTransaction(em ->
      em.createQuery("FROM Channel c WHERE c.room.id = :roomId", Channel.class)
        .setParameter("roomId", roomId)
        .getResultList()
      );
  }
}

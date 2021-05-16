package diskord.server.room;

import diskord.server.database.DatabaseManager;
import diskord.server.database.transactions.ChannelTransactions;
import diskord.server.database.transactions.RoomTransactions;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room {
  @Getter
  private final UUID id;
  @Getter
  private final String name;
  @Getter
  private final String description;
  @Getter
  private final String iconBase64;
  @Getter
  private final String joinId;
  @Getter
  @Setter
  private List<Channel> channels = new ArrayList<>();

  public Room(final UUID id, final String name, final String description, final String iconBase64) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.iconBase64 = iconBase64;
    this.joinId = id.toString().substring(0, 7);
  }

  public static List<Room> loadRooms(final DatabaseManager dbManager) {
    final List<diskord.server.database.room.Room> dbRooms = RoomTransactions.getRooms(dbManager);

    final List<Room> rooms = new ArrayList<>();

    for (final diskord.server.database.room.Room dbRoom : dbRooms) {
      final List<diskord.server.database.channel.Channel> dbChannels = ChannelTransactions.getChannelsByRoomId(dbManager, dbRoom.getId());

      final Room room = Room.map(dbRoom);

      final List<Channel> channels = new ArrayList<>();

      for (final diskord.server.database.channel.Channel dbChannel : dbChannels)
        channels.add(Channel.map(dbChannel));

      room.setChannels(channels);
    }

    return rooms;
  }

  public static Room map(final diskord.server.database.room.Room room) {
    return new Room(room.getId(), room.getName(), room.getDescription(), room.getIcon());
  }
}

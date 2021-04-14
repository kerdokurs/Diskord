package diskord.server.database.room;

import diskord.server.database.OldRepository;
import javassist.NotFoundException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class RoomRepository implements OldRepository<Room, UUID> {
  @Override
  public Room findOne(final @NotNull UUID uuid) {
    return null;
  }

  @Override
  public List<Room> findAll() {
    return null;
  }

  @Override
  public boolean save(@NotNull final Room room) {
    return false;
  }

  @Override
  public boolean delete(@NotNull final UUID uuid) throws NotFoundException {
    return false;
  }
}

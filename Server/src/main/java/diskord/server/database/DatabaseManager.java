package diskord.server.database;

import diskord.server.database.channel.ChannelRepository;
import diskord.server.database.user.UserRepository;
import lombok.Getter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseManager {
  @Getter
  private EntityManager entityManager;

  @Getter
  private UserRepository userRepository;

  @Getter
  private ChannelRepository channelRepository;

  public DatabaseManager() {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("DiskordServer.database");
    entityManager = factory.createEntityManager();

    userRepository = new UserRepository(entityManager);
    channelRepository = new ChannelRepository(entityManager);
  }
}

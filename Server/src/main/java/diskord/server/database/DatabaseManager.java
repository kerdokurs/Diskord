package diskord.server.database;

import diskord.server.database.message.MessageRepository;
import diskord.server.database.room.RoomRepository;
import diskord.server.database.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseManager {
  private static EntityManager entityManager;

  private static UserRepository userRepository;

  private static RoomRepository roomRepository;
  private static MessageRepository messageRepository;

  public static EntityManager entityManager() {
    if (entityManager == null) {
      final EntityManagerFactory factory = Persistence.createEntityManagerFactory("DiskordServer.database");
      entityManager = factory.createEntityManager();
    }

    return entityManager;
  }

  public static UserRepository userRepository() {
    if (userRepository == null)
      userRepository = new UserRepository(entityManager());

    return userRepository;
  }

  public static RoomRepository roomRepository() {
    if (roomRepository == null)
      roomRepository = new RoomRepository();

    return roomRepository;
  }

  public static MessageRepository messageRepository() {
    if (messageRepository == null)
      messageRepository = new MessageRepository();

    return messageRepository;
  }
}

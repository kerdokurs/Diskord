package diskord.server;

import diskord.server.jpa.user.User;
import diskord.server.jpa.user.UserRepository;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.UUID;

public class Server {
  private EntityManagerFactory factory;

  public Server() throws Exception {
    // FIXME: 17.03.2021 No persistence provider for EntityManager named DiskordServer.database not found.
    factory = Persistence.createEntityManagerFactory("DiskordServer.database");

    final Repository<User, UUID> userRepository = new UserRepository(factory);
    System.out.println(userRepository.findAll());

    final User user = new User("kerdo", "testing");
    System.out.println(user.getPassword());
    userRepository.save(user);
    System.out.println(userRepository.findAll());

    factory.close();
  }

  public static void main(final String[] args) throws Exception {
    new Server();
  }
}

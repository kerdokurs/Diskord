package diskord.server;

import diskord.server.controllers.LoginController;
import diskord.server.crypto.JWT;
import diskord.server.jpa.user.UserRepository;
import javassist.NotFoundException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Server {
  public Server() {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("DiskordServer.database");

    final UserRepository userRepository = new UserRepository(factory);
    System.out.println(userRepository.findAll());

    final LoginController loginController = new LoginController(userRepository);

    try {
      final String jws = loginController.handleLogin("kerdo", "testing");
      System.out.println(jws);

      System.out.println(JWT.validate(jws));
    } catch (final NotFoundException e) {
      System.out.println(e.getMessage());
    }

    factory.close();
  }

  public static void main(final String[] args) {
    new Server();
  }
}

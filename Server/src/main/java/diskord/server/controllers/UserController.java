package diskord.server.controllers;

import diskord.server.crypto.Hash;
import diskord.server.crypto.JWT;
import diskord.server.jpa.user.User;
import diskord.server.jpa.user.UserRepository;
import javassist.NotFoundException;

import java.util.HashMap;
import java.util.Map;

public class LoginController {
  private final UserRepository userRepository;

  public LoginController(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * See meetod handlib kasutaja registreerimise
   * TODO: Lisa siia ka jwt generatsioon ja edastus (?)
   *
   * @param username kasutajanimi
   * @param password parool
   */
  public void handleRegistration(final String username, final String password) {
    final User user = new User(username, password);

    // prolly vajab erindite viskamist, kuna hibernate ei viskab ise erindi.
    userRepository.save(user);
  }

  public String handleLogin(final String username, final String password) throws NotFoundException, IllegalArgumentException {
    final User user = userRepository.findOne(username);

    if (user == null) throw new NotFoundException("kasutaja ei leitud");

    final String hashedPassword = Hash.hash(password);

    if (!user.getPassword().equals(hashedPassword)) throw new IllegalArgumentException("vale parool");

    final Map<String, Object> claims = new HashMap<>();
    claims.put("admin", "true");
    final String jws = JWT.sign(user.getId().toString(), claims);
    return jws;
  }
}

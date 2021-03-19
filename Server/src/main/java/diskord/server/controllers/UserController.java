package diskord.server.controllers;

import diskord.server.crypto.Hash;
import diskord.server.crypto.JWT;
import diskord.server.jpa.user.User;
import diskord.server.jpa.user.UserRepository;
import javassist.NotFoundException;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class UserController {
  private final UserRepository userRepository;

  public UserController(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * See meetod handlib kasutaja registreerimise
   * TODO: Lisa siia ka jwt generatsioon ja edastus (?)
   *
   * @param username kasutajanimi
   * @param password parool
   */
  public void handleRegistration(@NotNull final String username, @NotNull final String password) {
    final User user = new User(username, password);

    // prolly vajab erindite viskamist, kuna hibernate ei viskab ise erindi.
    userRepository.save(user);
  }

  /**
   * See meetod handlib kasutaja sisse logimise
   * TODO: Arenda edasi, lõpeta parem versioon
   *
   * @param username kasutajanimi
   * @param password parool
   * @return sisselogimisel allkirjastatud jwt (jsonwebtoken)
   * @throws NotFoundException        visatakse, kui kasutajat ei leidu
   * @throws IllegalArgumentException visatakse, kui parool on vale
   */
  public String handleLogin(@NotNull final String username, @NotNull final String password) throws NotFoundException, IllegalArgumentException {
    final User user = userRepository.findOne(username);

    if (user == null) throw new NotFoundException("kasutaja ei leitud");

    final String hashedPassword = Hash.hash(password);

    if (!user.getPassword().equals(hashedPassword)) throw new IllegalArgumentException("vale parool");

    // TODO: Täpsem claimide seadmine (kas vaja id, roll vms)
    return JWT.sign(user.getId().toString(), Map.of());
  }
}

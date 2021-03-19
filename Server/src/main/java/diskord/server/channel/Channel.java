package diskord.server.channel;

import diskord.server.Payload;
import diskord.server.jpa.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements Runnable {
  @Getter
  private final List<ChannelUser> users = new ArrayList<>();

  @Getter
  @Setter
  @Accessors(
      chain = true
  )
  private UUID id;

  @Getter
  @Setter
  @Accessors(
      chain = true
  )
  private String name;

  public static Channel createChannel(final String name) {
    return new Channel().setId(UUID.randomUUID()).setName(name);
  }

  @Override
  public void run() {
    // TODO: Siin implementeerida socketid ja channelid kasutajatega suhtlemiseks
    // Igal kanalil (chati channel) on oma port, mis on peaserveri omast erinev.
    // Nii on mu arust kõige mõistlikum kanaleid hallata, kui igaüks on oma lõimu
    // ja pordi peal.
  }

  public ChannelUser addUser(final User user) {
    // Võib-olla pole seda meetodit vaja, kuna JPA peaks hoidma vajalikke asju mälus,
    // siis on võimalik vajadusel tema käest ID järgi infot saada, ilma et peaks andmebaasi
    // poole pöörduma.
    final ChannelUser channelUser = ChannelUser.createChannelUser(user);

    users.add(channelUser);

    return channelUser;
  }

  public void removeUser(final UUID id) {
    users.removeIf(user -> user.getId().equals(id));
  }

  public void handlerPayload(final Payload payload) {
    // Siin tuleks tegeleda payloadiga. Payload on juba deserialiseeritud
    // (võiks olla channelist lugemist ajal vms).
  }

  public void broadcast(final String message) {
    // TODO: Siin saata kõikidele kasutajatele sõnum
    // nt users.forEach(user -> send(user.getId(), message)) vms
  }

  public void send(final UUID id, final String message) {
    // TODO: Siin saata ainult ühele kasutajale sõnum
  }

  // TODO: Siia panna implementatsiooniks vajalikud meetoid (receive, send jms)
  // võib-olla mõistlik lisada kuskile eraldi klassi ja siin kasutada vms
}

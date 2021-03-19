package diskord.server;

import diskord.server.channel.Channel;
import diskord.server.channel.ChannelLoader;
import diskord.server.jpa.channel.ChannelRepository;
import diskord.server.jpa.user.User;
import diskord.server.jpa.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Server {
  private final Logger logger = Logger.getLogger(getClass().getName());

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;

  private List<Channel> channels;

  public Server() {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("DiskordServer.database");
    final EntityManager em = factory.createEntityManager();

    channelRepository = new ChannelRepository(em);
    userRepository = new UserRepository(em);

    init();
  }

  public static void main(String[] args) {
    final Server server = new Server();
    server.start();
  }

  public void init() {
    channels = ChannelLoader.loadChannels(channelRepository);
  }

  public void start() {
    logger.info("server has started");
    // Siin teha serveri pealõim ja socket ning kanal, mis haldavad
    // suhtlust põhiserveriga.
    // Põhiserver (this) majandab huvilistele kanalite info jms
    // saatmisega ning vastuvõtuga (loo mulle kanal, ava kanal vms).
  }

  public void createChannel(final String name, final User owner) {
    // Siin loome suvalise kanali ja salvestame andmebaasi
    final Channel channel = Channel.createChannel(name);
    channelRepository.save(
        new diskord.server.jpa.channel.Channel()
            .setName(name)
            .setOwner(owner)
    );
  }

  public void startChannel(final UUID id) {
    // TODO: Siin startida antud IDga kanal (kui puudub, ei tee midagi)
    // Iga kanal on oma lõim, mis haldab oma kliente, sõnumeid jms ise.
  }
}

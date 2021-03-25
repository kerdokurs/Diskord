package diskord.server;

import diskord.server.channel.Channel;
import diskord.server.database.DatabaseManager;
import diskord.server.database.user.User;
import diskord.server.payload.Payload;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.logging.Logger;

public abstract class Server {
  private final Logger logger = Logger.getLogger(getClass().getName());
  private final DatabaseManager dbManager;

  protected Selector selector;
  protected ServerSocketChannel serverSocketChannel;
  protected Map<SocketChannel, Queue<Payload>> socketMap = new HashMap<>();

  private InetSocketAddress socketAddress;

  protected Server(final int port) {
    socketAddress = new InetSocketAddress("localhost", port);

    dbManager = new DatabaseManager();
  }

  public void init() throws IOException {
    // Setting up selector and server socket channel
    selector = SelectorProvider.provider().openSelector();

    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);

    serverSocketChannel.socket().bind(socketAddress);
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  }

  public void start() throws IOException {
    init();

    logger.info("server has started");

    while (running) {
      // This 'running' is stupid in the case where selector.select() is blocking
      selector.select();

      final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

      while (running && keys.hasNext()) {
        final SelectionKey key = keys.next();
        keys.remove();

        if (!key.isValid()) continue;

        if (key.isAcceptable()) accept(key);
        if (key.isReadable()) read(key);
        if (key.isWritable()) write(key);
      }
    }
  }

  // Does not work yet
  // TODO: Fix.
  public void stop() {
    try {
      logger.info("shutting server down");

      for (final SocketChannel socketChannel : socketMap.keySet())
        socketChannel.close();

      serverSocketChannel.close();
      selector.close();

      logger.info("server has shut down");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  // Does not work yet
  // TODO: Fix.
  public void stop() {
    try {
      logger.info("shutting server down");

      for (SocketChannel socketChannel : socketMap.keySet())
        socketChannel.close();

      serverSocketChannel.close();
      selector.close();

      logger.info("server has shut down");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  private void accept(final SelectionKey key) throws IOException {
    final ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
    final SocketChannel channel = serverChannel.accept();
    channel.configureBlocking(false);

    final Socket socket = channel.socket();
    final SocketAddress socketAddress = socket.getRemoteSocketAddress(); // TODO: See panna äkki mingisse klassi, mis hoiab endas ka kasutajat vms

    logger.info(String.format("connection from %s", socketAddress));

    socketMap.put(channel, new ArrayDeque<>());
    channel.register(selector, SelectionKey.OP_READ);
  }

  private void read(final SelectionKey key) throws IOException {
    final SocketChannel channel = (SocketChannel) key.channel();

    if (!channel.isOpen()) {
      logger.warning("channel is closed, skipping");
      return;
    }

    final ByteBuffer sizeBuffer = ByteBuffer.allocate(4); // allokeerime mälu payloadi pikkuse jaoks

    int numRead = channel.read(sizeBuffer);

    if (numRead == 0) return;

    if (numRead == -1) {
      final Socket socket = channel.socket();
      final SocketAddress socketAddress = socket.getRemoteSocketAddress();

      logger.info(String.format("%s disconnected", socketAddress));

      channel.close();
      key.cancel();

      return;
    }

    sizeBuffer.flip();
    final int dataSize = sizeBuffer.getInt();

    final ByteBuffer jsonBuffer = ByteBuffer.allocate(dataSize);
    channel.read(jsonBuffer);

    jsonBuffer.flip();

    final String jsonData = new String(jsonBuffer.array());
    final Payload payload = Payload.fromJson(jsonData);

    handlePayload(payload, key);
  }

  private void write(final SelectionKey key) throws IOException {
    final SocketChannel channel = (SocketChannel) key.channel();

    read(key);

    final Queue<Payload> payloads = socketMap.get(channel);
    if (payloads == null || payloads.isEmpty()) return;

    final Payload payload = payloads.poll();

    if (payload == null) {
      read(key);
      return;
    }

    final byte[] payloadData = payload.toJson().getBytes();
    final int payloadSize = payloadData.length;

    final ByteBuffer jsonBuffer = ByteBuffer.allocate(4 + payloadSize);
    jsonBuffer.putInt(payloadSize);
    jsonBuffer.put(payloadData);

    jsonBuffer.flip();

    channel.write(jsonBuffer);
  }

  public void createChannel(final String name, final User owner) {
    // Siin loome suvalise kanali ja salvestame andmebaasi
    final Channel channel = Channel.createChannel(name);
    dbManager.getChannelRepository().save(
        new diskord.server.database.channel.Channel()
            .setName(name)
            .setOwner(owner)
    );
  }

  public void startChannel(final UUID id) {
    // TODO: Siin startida antud IDga kanal (kui puudub, ei tee midagi)
    // Iga kanal on oma lõim, mis haldab oma kliente, sõnumeid jms ise.
  }

  protected abstract void handlePayload(final Payload payload, final SelectionKey key) throws ClosedChannelException;

  /**
   * Method for finding a port in a fixed range (if we don't want ServerSocket to choose its own)
   *
   * @param portRangeArray - int[]
   * @return ServerSocket
   * @throws IOException
   */
  public ServerSocket findAndCreateAvailablePort(int[] portRangeArray) throws IOException {
    for (int port : portRangeArray) {//loops through a range of ints
      try { //tries to create a port in the given index
        return new ServerSocket(port);
      } catch (IOException e) { //if port is allocated, continues to the next index in portRangeArray
        continue;
      }
    }

    throw new IOException("No free port found.");
  }
}

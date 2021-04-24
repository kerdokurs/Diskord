package diskord.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.server.channel.Channel;
import diskord.server.database.DatabaseManager;
import diskord.server.database.user.User;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public abstract class Server implements Runnable {
  protected final Logger logger = LogManager.getLogger();
  protected final DatabaseManager dbManager;
  protected final InetSocketAddress socketAddress;
  protected Selector selector;
  protected ServerSocketChannel serverSocketChannel;
  protected Map<SocketChannel, Queue<Payload>> socketMap = new HashMap<>();
  protected ObjectMapper mapper = new ObjectMapper();

  /**
   * @param port      port that the server should run on
   * @param dbManager the only instance of database manager
   */
  protected Server(final int port, final DatabaseManager dbManager) {
    socketAddress = new InetSocketAddress("localhost", port);
    this.dbManager = dbManager;
  }

  public void init() throws IOException {
    // Setting up selector and server socket channel
    selector = SelectorProvider.provider().openSelector();

    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);

    serverSocketChannel.socket().bind(socketAddress);
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  }

  @Override
  public void run() {
    try {
      init();

      logger.info("server has started");

      // Use Thread#interrupt to kill the server.
      while (!Thread.currentThread().isInterrupted()) {
        selector.select();

        final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

        while (keys.hasNext()) {
          final SelectionKey key = keys.next();
          keys.remove();

          if (!key.isValid()) continue;

          if (key.isAcceptable()) accept(key);
          if (key.isReadable()) read(key);
          if (key.isWritable()) write(key);
        }
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
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

      // database manager must be closed after using it
      dbManager.close();

      Thread.currentThread().interrupt();

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

    logger.info(() -> String.format("connection from %s", socketAddress));

    socketMap.put(channel, new ArrayDeque<>());
    channel.register(selector, SelectionKey.OP_READ);
  }

  private void read(final SelectionKey key) throws IOException {
    final SocketChannel channel = (SocketChannel) key.channel();

    if (!channel.isOpen()) {
      logger.warn("channel is closed, skipping");
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
    final Payload payload = Payload.fromJson(mapper, jsonData);

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

    final byte[] payloadData = payload.toJson(mapper).getBytes();
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
  }

  public void startChannel(final UUID id) {
    // TODO: Siin startida antud IDga kanal (kui puudub, ei tee midagi)
    // Iga kanal on oma lõim, mis haldab oma kliente, sõnumeid jms ise.
  }

  protected abstract void handlePayload(final Payload payload, final SelectionKey key) throws ClosedChannelException;

  protected Payload unhandledPayload(final Payload payload, final SelectionKey key) {
    return new Payload()
      .setType(PayloadType.INVALID)
      .setResponseTo(payload.getId())
      .putBody("message", "unhandled payload");
  }

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

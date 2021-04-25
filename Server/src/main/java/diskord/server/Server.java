package diskord.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import diskord.server.channel.Channel;
import diskord.server.database.DatabaseManager;
import diskord.server.database.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public abstract class Server implements Runnable {
  protected final Logger logger = LogManager.getLogger();
  protected final DatabaseManager dbManager;
  protected final InetSocketAddress socketAddress;
  protected Selector selector;
  protected ServerSocketChannel serverSocketChannel;
  protected Map<SocketChannel, Queue<ByteBuffer>> socketMap = new HashMap<>();
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
    // serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    final int validOps = serverSocketChannel.validOps();
    serverSocketChannel.register(selector, validOps);
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

          try {
            if (key.isAcceptable()) accept(key);
            if (key.isReadable()) read(key);
            if (key.isWritable() && !socketMap.get((SocketChannel) key.channel()).isEmpty()) write(key);
          } catch (final Exception e) {
            System.out.println(e);
          }
        }
      }
    } catch (final IOException e) {
      System.out.println("error");
//      throw new UncheckedIOException(e);
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
//    final ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
    final SocketChannel channel = serverSocketChannel.accept();
    channel.configureBlocking(false);

    final Socket socket = channel.socket();
    final SocketAddress socketAddress = socket.getRemoteSocketAddress(); // TODO: See panna äkki mingisse klassi, mis hoiab endas ka kasutajat vms

    logger.info(() -> String.format("connection from %s", socketAddress));

    socketMap.put(channel, new ArrayDeque<>(10));
    channel.register(selector, SelectionKey.OP_READ);
  }

  private void read(final SelectionKey key) throws IOException {
    final SocketChannel channel = (SocketChannel) key.channel();
    logger.info(() -> String.format("incoming data from %s", channel));

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

    logger.info(() -> String.format("payload received: %s", payload));

    final Payload response = handlePayload(payload);
    final ByteBuffer buffer = payloadToByteBuffer(response);
    socketMap.get(channel).add(buffer);
    channel.register(selector, SelectionKey.OP_WRITE);
  }

  private void write(final SelectionKey key) throws IOException {
    final SocketChannel channel = (SocketChannel) key.channel();
    logger.info(() -> String.format("trying to write to %s", channel));

    final Queue<ByteBuffer> buffers = socketMap.get(channel);
    if (buffers == null || buffers.isEmpty()) return;

    final ByteBuffer buffer = buffers.poll();

    if (buffer == null) {
      return;
    }

    logger.info(() -> String.format("writing %s%n to %s", buffer, channel));

    channel.write(buffer);
  }

  private ByteBuffer payloadToByteBuffer(final Payload payload) throws JsonProcessingException {
    final byte[] payloadData = payload.toJson(mapper).getBytes();
    final int payloadSize = payloadData.length;

    final ByteBuffer buffer = ByteBuffer.allocate(4 + payloadSize);
    buffer.putInt(payloadSize);
    buffer.put(payloadData);

    buffer.flip();

    return buffer;
  }

  protected abstract Payload handlePayload(final Payload payload);

  protected Payload unhandledPayload(final Payload payload) {
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

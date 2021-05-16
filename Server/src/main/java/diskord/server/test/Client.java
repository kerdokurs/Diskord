package diskord.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.payload.Payload;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Client {
  private final InetSocketAddress address;

  private final ObjectMapper mapper = new ObjectMapper();

  @Getter
  private SocketChannel channel;

  // should we have a blocking queue for handling the incoming payloads?
  // if not, remove this.
  @Getter
  private BlockingQueue<Payload> payloads = new SynchronousQueue<>();

  private BlockingQueue<Payload> dataToSend = new SynchronousQueue<>();

  // This should run on a new thread separate from the UI
  protected Client(final InetSocketAddress address) {
    this.address = address;

    try {
      this.channel = SocketChannel.open(address);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
//    System.out.println(Auth.encode("kerdo", Map.of()));
//    final InetSocketAddress address = new InetSocketAddress("localhost", 8192);
//    Client client = new Client(address);
//
//    final Payload payload = new Payload().setType(PayloadType.LOGIN).putBody("username", "kerdo").putBody("password", "testing"); // demo reading a payload
//
//    for (int i = 0; i < 4; i++) {
//      client.write(payload);
//      System.out.println(client.read());
//    }
//
//
//    client.getChannel().close();

    final byte[] data = Files.readAllBytes(Path.of("NALI.png"));
    System.out.println(Base64.getEncoder().encodeToString(data));
  }

  public void run() {
    try (final SocketChannel channel = SocketChannel.open(address)) {
      this.channel = channel;

      System.out.println("client has started");

      while (!Thread.currentThread().isInterrupted()) {
        // idk if we should read all the data we can and process it on the go
        // or wait for a read and for a write
//        payloads.offer(read());
      }

      // channel will be closed after the while loop exists by the twr block
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void stop() {
    Thread.currentThread().interrupt();

    System.out.println("client has stopped");
  }

  // See https://github.com/kerdokurs/Diskord/blob/6e015303cd5af564638c8a13da237fcd7eacae10/Server/src/main/java/diskord/server/Server.java#L107
  // for more info on how the server channel is implemented.
  // Keep in mind that we will only have a single channel on this client.
  public Payload read() throws IOException {
    final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
    channel.read(sizeBuffer);
    sizeBuffer.flip();

    final int size = sizeBuffer.getInt();

    final ByteBuffer messageBuffer = ByteBuffer.allocate(size);
    channel.read(messageBuffer);
    messageBuffer.flip();

    final String message = new String(messageBuffer.array());
    return Payload.fromJson(mapper, message);
  }

  public void write(final Payload payload) throws IOException {
    write(payload.toJson(mapper).getBytes());
  }

  public void write(final byte[] data) throws IOException {
    final ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
    buffer.putInt(data.length);
    buffer.put(data);
    buffer.flip();

    channel.write(buffer);
  }
}

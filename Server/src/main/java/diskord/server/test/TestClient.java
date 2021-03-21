package diskord.server.test;

import diskord.server.payload.Payload;
import diskord.server.payload.PayloadType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class TestClient {
  public static void main(final String[] args) throws IOException {
    InetSocketAddress address = new InetSocketAddress("localhost", 8192);

    try (final SocketChannel client = SocketChannel.open(address)) {

      System.out.println("client has started");

      final Payload payload = new Payload().setId(UUID.randomUUID()).setType(PayloadType.BINK);

      System.out.printf("sending: %s%n", payload);

      final byte[] payloadData = payload.toJson().getBytes();
      final int size = payloadData.length;

      final ByteBuffer buffer = ByteBuffer.allocate(size + 4);
      buffer.putInt(size);
      buffer.put(payloadData);

      buffer.flip();

      client.write(buffer);

      read(client);
    }
  }

  private static void read(final SocketChannel client) throws IOException {
    System.out.println("read1");
    final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
    client.read(sizeBuffer);

    sizeBuffer.flip();

    final int size = sizeBuffer.getInt();

    final ByteBuffer messageBuffer = ByteBuffer.allocate(size);
    client.read(messageBuffer);
    messageBuffer.flip();

    final String message = new String(messageBuffer.array());
    final Payload payload = Payload.fromJson(message);

    System.out.printf("received: %s%n", payload);
  }
}

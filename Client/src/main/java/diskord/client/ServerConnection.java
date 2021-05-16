package diskord.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import diskord.client.controllers.Controller;
import diskord.payload.Payload;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerConnection implements Runnable{

    private List<Controller> passiveListeners = new ArrayList<>();
    private HashMap<UUID,Controller> responseWaitingControllers = new HashMap<>();
    private final InetSocketAddress address;
    private Logger logger = LogManager.getLogger(getClass().getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private SocketChannel channel;
    @Getter
    private Deque<Payload> payloadsToRecive = new ArrayDeque<>();
    @Getter
    private Deque<Payload> payloadsToSend = new ArrayDeque<>();


    // This should run on a new thread separate from the UI
    public ServerConnection(final InetSocketAddress address) {
        this.address = address;
    }

    /**
     * Method that sends payload to server and adds listener for server response.
     * @param payload The payload that is sent to server
     * @param controller The controller that is called when server responds
     * @throws IOException
     */
    public void writeWithResponse(Payload payload, Controller controller) throws IOException {
        //TODO Handle IOException
        responseWaitingControllers.put(payload.getId(),controller);
        write(payload);
    }

    /**
     * Method adds controller to passive listener. When server sends payload that
     * was not expected, it will get all controllers that are waiting for that kind of
     * payload types.
     * @param controller Controller that is added to passive listener.
     */
    public void addListener(Controller controller){
        passiveListeners.add(controller);
    }

    /**
     * Method removes controller from passive listener.
     * @param controller The controller that is removed
     */
    public void removeListeners(Controller controller){
        passiveListeners.remove(controller);
    }

    @Override
    public void run() {

        try (final SocketChannel channel = SocketChannel.open(address)) {

            this.channel = channel;
            //channel.configureBlocking(false);


            while (!Thread.currentThread().isInterrupted()) {
                // idk if we should read all the data we can and process it on the go
                // or wait for a read and for a write
                //payloads.offer(read());
                if(channel.isConnected()){
                    final Payload payload = read();
                    //TODO Handle return types.!
                    responseWaitingControllers.get(payload.getResponseTo()).handleResponse(payload);
                    responseWaitingControllers.remove(payload.getResponseTo());
                }
            }

            // channel will be closed after the while loop exists by the twr block
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // No idea
    public void stop() {
        Thread.currentThread().interrupt();
    }


    public Payload read() throws IOException {
        // TODO: Add checks if reading is possible
        final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        channel.read(sizeBuffer);
        sizeBuffer.flip();

        final int size = sizeBuffer.getInt();

        final ByteBuffer messageBuffer = ByteBuffer.allocate(size);
        channel.read(messageBuffer);
        messageBuffer.flip();

        final String message = new String(messageBuffer.array());
        logger.info(() -> message);
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

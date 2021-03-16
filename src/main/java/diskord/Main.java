package diskord;

import diskord.client.Client;
import diskord.server.Server;

import java.util.List;

public class Main {
  public static void main(final String[] args) {
    final boolean isServer = List.of(args).contains("--server");

    if (isServer)
      new Server();
    else new Client();
  }
}

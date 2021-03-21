package diskord.server.cli;

import diskord.server.Server;

import java.util.Scanner;

public class CLI implements Runnable {
  private final Server server;

  public CLI(final Server server) {
    this.server = server;
  }

  @Override
  public void run() {
    final Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.print("> ");
      final String command = scanner.nextLine();
      final String[] tokens = command.split(" ");

      if (tokens.length == 0) continue;

      if ("stop".equals(tokens[0])) {
        server.stop(); // completely does not work.
      } else System.out.printf("> %s%n", command);
    }
  }
}

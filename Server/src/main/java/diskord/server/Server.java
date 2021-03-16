package diskord.server;

import javafx.application.Application;
import javafx.stage.Stage;

public class Server extends Application {
  public Server() {
    System.out.println("server has started");
  }

  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage stage) throws Exception {
    stage.show();
  }
}

package diskord.client;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.UUID;

public class Client extends Application {
  @Getter
  private UUID uuid;

  public Client() {
    System.out.println("client has started");
  }

  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage stage) throws Exception {
    stage.show();
  }
}

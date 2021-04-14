import diskord.server.controllers.AuthenticationController;
import diskord.server.payload.Payload;
import org.junit.jupiter.api.Test;

import static diskord.server.payload.PayloadType.LOGIN;
import static diskord.server.payload.PayloadType.REGISTER_ERROR;
import static org.junit.Assert.assertEquals;

public class AuthenticationControllerTests {
  @Test
  public void testEmptySignUpPayload() {
    final Payload payload = new Payload()
      .setType(LOGIN);

    final Payload response = AuthenticationController
      .handleSignUp(payload);

    assertEquals(REGISTER_ERROR, response.getType());
  }
}

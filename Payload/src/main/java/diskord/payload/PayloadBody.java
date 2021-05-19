package diskord.payload;

import java.io.Serializable;
import java.util.HashMap;

public class PayloadBody extends HashMap<String, Object> implements Serializable {
  // Key names where in the body should specific data be stored.
  public static final transient String BODY_FIELD = "field";
  public static final transient String BODY_TOKEN = "token";
  public static final transient String BODY_MESSAGE = "message";

  public static final transient String BODY_USERNAME = "username";
  public static final transient String BODY_PASSWORD = "password";

  public static final transient String BODY_INVALID = "invalid request";

  public static final transient String SERVER_ID = "join_id";
}



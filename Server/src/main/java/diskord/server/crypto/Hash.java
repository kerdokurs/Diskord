package diskord.server.crypto;

import org.apache.commons.codec.digest.DigestUtils;

public class Hash {
  public static String hash(final String data) {
    return DigestUtils.sha256Hex(data);
  }
}

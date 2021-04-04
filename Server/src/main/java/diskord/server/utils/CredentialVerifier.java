package diskord.server.utils;

import diskord.server.utils.credentials.CredentialConstraint;
import diskord.server.utils.credentials.CredentialError;

public class CredentialVerifier {
  public static CredentialError verify(String data, CredentialConstraint... constraints) {
//    if(data == null){
//      return CredentialError.NULL_ERROR;
//    }
//

    for (final CredentialConstraint constraint : constraints) {
      if (!constraint.getMethod().isValid(data)) {
        return constraint.getError();
      }
    }

    return CredentialError.NONE;
  }
}

package diskord.server.utils;

import diskord.server.utils.credentials.CredentialConstraint;

public class CredentialVerifier {
  public static String verify(String data, CredentialConstraint... constraints) {
//    if(data == null){
//      return CredentialError.NULL_ERROR;
//    }
//

    for (final CredentialConstraint constraint : constraints) {
      if (!constraint.getMethod().isValid(data)) {
        return constraint.getMessage();
      }
    }

    return null;
  }
}

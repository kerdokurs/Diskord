package diskord.server.utils;

import diskord.server.utils.credentials.CredentialConstraint;

public class CredentialValidator {
  private CredentialValidator() {
  }

  /**
   * Validates given data against provided constraints
   *
   * @param data data to validate
   * @param constraints list of constraints
   * @return message of first constraint that was violated
   */
  public static String validate(String data, CredentialConstraint... constraints) {
    for (final CredentialConstraint constraint : constraints) {
      if (!constraint.getMethod().isValid(data)) {
        return constraint.getMessage();
      }
    }

    return null;
  }
}

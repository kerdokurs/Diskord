package diskord.server.utils.credentials;

@FunctionalInterface
public interface CredentialLambda {
  /**
   * Lambda that can be used to specify whether input is valid
   *
   * @param data input string
   * @return validity of input
   */
  boolean isValid(final String data);
}

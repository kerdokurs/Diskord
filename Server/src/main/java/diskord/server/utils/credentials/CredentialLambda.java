package diskord.server.utils.credentials;

public interface CredentialLambda {
  boolean isValid(final String input);
}

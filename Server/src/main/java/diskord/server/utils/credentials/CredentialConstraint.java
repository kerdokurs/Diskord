package diskord.server.utils.credentials;

import lombok.Getter;

import java.util.Objects;

public class CredentialConstraint {
  public static final CredentialConstraint NULL_CONSTRAINT = new CredentialConstraint(
    CredentialError.NULL_ERROR,
    Objects::nonNull
  );

  public static final CredentialConstraint USERNAME_LENGTH_CONSTRAINT = new CredentialConstraint(
    CredentialError.NULL_ERROR,
    input -> input.length() >= 6
  );

  @Getter
  private final CredentialError error;

  @Getter
  private final CredentialLambda method;

  public CredentialConstraint(final CredentialError error, final CredentialLambda method) {
    this.error = error;
    this.method = method;
  }
}

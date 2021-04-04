package diskord.server.utils.credentials;

import lombok.Getter;

import java.util.Objects;

public class CredentialConstraint {
  public static final CredentialConstraint NULL_CONSTRAINT = new CredentialConstraint(
    CredentialError.NULL_ERROR,
    Objects::nonNull,
    "must not be null"
  );

  public static final CredentialConstraint TOO_SHORT_CONSTRAINT = new CredentialConstraint(
    CredentialError.NULL_ERROR,
    input -> input.length() >= 4,
    "length must be greater than 4"
  );

  public static final CredentialConstraint TOO_LONG_CONSTRAINT = new CredentialConstraint(
    CredentialError.LENGTH_ERROR,
    input -> input.length() <= 50,
    "length must be less than 50"
  );

  @Getter
  private final CredentialError error;

  @Getter
  private final CredentialLambda method;

  @Getter
  private final String message;

  /**
   * Constraint for validating data
   *
   * @param error error which will be returned when input data is not valid
   * @param method method that will be used to check the validity of the input data
   * @param message message that will be returned when data is not valid
   */
  public CredentialConstraint(final CredentialError error, final CredentialLambda method, final String message) {
    this.error = error;
    this.method = method;
    this.message = message;
  }
}

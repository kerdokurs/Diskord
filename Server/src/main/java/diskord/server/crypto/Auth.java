package diskord.server.crypto;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

public class Auth {
  private final static Algorithm algorithm = Algorithm.HMAC256("secret"); // TODO: put this in config
  private final static JWTVerifier verifier = JWT.require(algorithm).withIssuer("diskord").build();

  public static String encode(@NotNull final String subject, @NotNull final Map<String, Object> claims) {
    return JWT.create()
      .withSubject(subject)
      .withIssuer("diskord")
      .withIssuedAt(new Date())
      .withPayload(claims)
      .sign(algorithm);
  }

  public static DecodedJWT decode(@NotNull final String token) throws JWTVerificationException {
    return verifier.verify(token);
  }
}

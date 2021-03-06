package diskord.server.crypto;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

public class Auth {
  private final static Algorithm algorithm = Algorithm.HMAC256("secret"); // TODO: put this in config
  private final static JWTVerifier verifier = JWT.require(algorithm).withIssuer("diskord").build();

  public static String encode(final String subject) {
    return JWT.create()
      .withSubject(subject)
      .withIssuer("diskord")
      .withIssuedAt(new Date())
      .sign(algorithm);
  }

  public static DecodedJWT decode(final String token) throws JWTVerificationException {
    return verifier.verify(token);
  }
}

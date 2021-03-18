package diskord.server.crypto;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Map;

public class JWT {
  public static final Key JWT_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // pane see kuskile configi

  /**
   * Allkirjastame jwt-i.
   *
   * @param subject kasutaja id
   * @param claims  kasutaja claims
   * @return jws
   */
  public static String sign(final String subject, final Map<String, Object> claims) {
    return Jwts.builder()
        .setSubject(subject)
        .addClaims(claims)
        .signWith(JWT_KEY)
        .compact();
  }

  /**
   * Valideerime allkirjastatud jwt-i.
   *
   * @param jws allkirjastatud jwt
   * @return jwt-i sisu
   */
  public static Claims validate(final String jws) {
    try {
      final Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(JWT_KEY).build().parseClaimsJws(jws);
      final Claims body = claims.getBody();
      return body;
    } catch (final JwtException jwtException) {
      return null;
    }
  }
}

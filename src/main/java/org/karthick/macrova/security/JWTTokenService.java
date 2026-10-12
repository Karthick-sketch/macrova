package org.karthick.macrova.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class JWTTokenService {

  @Value("${jwt.secret}")
  private String secretKey;

  @NonNull
  public String generateToken(String subject, long expiresAt) {
    return Objects.requireNonNull(
      JWT.create()
        .withSubject(subject)
        .withExpiresAt(new Date(System.currentTimeMillis() + expiresAt))
        .sign(Algorithm.HMAC512(secretKey))
    );
  }

  public String validateToken(String bearerToken) {
    String token = bearerToken.replace(SecurityConstants.BEARER, "").trim();
    return JWT.require(Algorithm.HMAC512(secretKey))
      .build()
      .verify(token)
      .getSubject();
  }
}

package org.karthick.macrova.user.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.AllArgsConstructor;
import org.karthick.macrova.exception.UnauthorizedException;
import org.karthick.macrova.security.JWTTokenService;
import org.karthick.macrova.security.SecurityConstants;
import org.karthick.macrova.security.UserSession;
import org.karthick.macrova.user.dto.AccessTokenDTO;
import org.karthick.macrova.user.dto.UserDTO;
import org.karthick.macrova.user.dto.UsernameDTO;
import org.karthick.macrova.user.entity.User;
import org.karthick.macrova.user.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;
  private final JWTTokenService jwtTokenService;

  private final UserSession userSession;

  @GetMapping("/username")
  public UsernameDTO getUser() {
    return new UsernameDTO(userSession.getAuthenticatedUsername());
  }

  @PostMapping("/register")
  public UserDTO register(@RequestBody User user) {
    return userService.registerUser(user);
  }

  @PostMapping("/refresh")
  public AccessTokenDTO requestAccessToken(
    @CookieValue(
      name = SecurityConstants.REFRESH_TOKEN_HEADER,
      required = false
    ) String refreshToken
  ) {
    if (refreshToken == null) {
      throw new UnauthorizedException("Refresh token missing");
    }
    try {
      String username = jwtTokenService.validateToken(refreshToken);
      String newAccessToken = jwtTokenService.generateToken(
        username,
        SecurityConstants.ACCESS_TOKEN_EXPIRATION
      );
      return new AccessTokenDTO(newAccessToken);
    } catch (JWTVerificationException e) {
      throw new UnauthorizedException("Invalid or expired refresh token");
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    ResponseCookie cookie = ResponseCookie.from(
      SecurityConstants.REFRESH_TOKEN_HEADER,
      ""
    )
      .httpOnly(true)
      .sameSite("Lax")
      .secure(false)
      .path("/")
      .maxAge(0)
      .build();
    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, cookie.toString())
      .build();
  }
}

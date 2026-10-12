package org.karthick.macrova.security.manager;

import java.util.Collections;
import lombok.AllArgsConstructor;
import org.karthick.macrova.exception.EntityNotFoundException;
import org.karthick.macrova.user.entity.User;
import org.karthick.macrova.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication)
    throws AuthenticationException {
    User user;
    try {
      user = userService.findUserByUsername(authentication.getName());
    } catch (EntityNotFoundException e) {
      throw new BadCredentialsException("Bad credentials");
    }
    if (
      !passwordEncoder.matches(
        authentication.getCredentials().toString(),
        user.getPassword()
      )
    ) {
      throw new BadCredentialsException("Bad credentials");
    }
    return new UsernamePasswordAuthenticationToken(
      authentication.getName(),
      null,
      Collections.emptyList()
    );
  }
}

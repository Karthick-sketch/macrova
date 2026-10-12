package org.karthick.macrova.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.AllArgsConstructor;
import org.karthick.macrova.security.JWTTokenService;
import org.karthick.macrova.security.SecurityConstants;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

  private final JWTTokenService jwtTokenService;

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String path = request.getServletPath();
    if (
      path.equals(SecurityConstants.REGISTER_PATH) ||
      path.equals(SecurityConstants.REFRESH_PATH)
    ) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader(SecurityConstants.AUTHORIZATION);
    if (header != null && header.startsWith(SecurityConstants.BEARER)) {
      String username = jwtTokenService.validateToken(header);
      Authentication authentication = new UsernamePasswordAuthenticationToken(
        username,
        null,
        Collections.emptyList()
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }
}

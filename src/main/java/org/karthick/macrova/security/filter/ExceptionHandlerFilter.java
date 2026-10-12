package org.karthick.macrova.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (TokenExpiredException e) {
      setExceptionResponse(
        response,
        HttpServletResponse.SC_UNAUTHORIZED,
        "Access token expired"
      );
    } catch (JWTVerificationException e) {
      setExceptionResponse(
        response,
        HttpServletResponse.SC_UNAUTHORIZED,
        "JWT verification failed"
      );
    } catch (RuntimeException e) {
      setExceptionResponse(
        response,
        HttpServletResponse.SC_BAD_REQUEST,
        "Bad request"
      );
    }
  }

  private void setExceptionResponse(
    HttpServletResponse response,
    int status,
    String message
  ) throws IOException {
    response.setStatus(status);
    response.getWriter().write(message);
    response.getWriter().flush();
  }
}

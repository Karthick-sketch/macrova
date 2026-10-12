package org.karthick.macrova.security;

import java.util.List;
import lombok.AllArgsConstructor;
import org.karthick.macrova.security.filter.AuthenticationFilter;
import org.karthick.macrova.security.filter.ExceptionHandlerFilter;
import org.karthick.macrova.security.filter.JWTAuthenticationFilter;
import org.karthick.macrova.security.manager.CustomAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

  private final CustomAuthenticationManager customAuthenticationManager;
  private final JWTTokenService jwtTokenService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
    throws Exception {
    AuthenticationFilter authenticationFilter = new AuthenticationFilter(
      customAuthenticationManager,
      jwtTokenService
    );
    authenticationFilter.setFilterProcessesUrl("/authenticate");
    http
      .csrf(csrf -> csrf.disable())
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .authorizeHttpRequests(authorizeRequests ->
        authorizeRequests
          .requestMatchers(
            HttpMethod.POST,
            SecurityConstants.REGISTER_PATH,
            SecurityConstants.REFRESH_PATH
          )
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
      .addFilter(authenticationFilter)
      .addFilterAfter(
        new JWTAuthenticationFilter(jwtTokenService),
        AuthenticationFilter.class
      )
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      );
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
    corsConfiguration.setAllowedMethods(
      List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
    );
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setExposedHeaders(
      List.of(
        SecurityConstants.AUTHORIZATION,
        SecurityConstants.REFRESH_TOKEN_HEADER
      )
    );
    corsConfiguration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}

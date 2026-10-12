package org.karthick.macrova.security;

public final class SecurityConstants {

  public static final long ACCESS_TOKEN_EXPIRATION = 3600000L;
  public static final long REFRESH_TOKEN_EXPIRATION = 86400000L;

  public static final String BEARER = "Bearer ";
  public static final String AUTHORIZATION = "Authorization";
  public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";

  public static final String REGISTER_PATH = "/user/register";
  public static final String REFRESH_PATH = "/user/refresh";

  private SecurityConstants() {}
}

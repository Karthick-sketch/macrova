package org.karthick.macrova.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.karthick.macrova.security.SecurityConstants;

@Getter
@NoArgsConstructor
public class AccessTokenDTO {

  private String accessToken;

  public AccessTokenDTO(String accessToken) {
    setAccessToken(accessToken);
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = SecurityConstants.BEARER + accessToken;
  }
}

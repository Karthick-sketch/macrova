package org.karthick.macrova.aichatservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageModel {

  private String role;
  private String content;
}

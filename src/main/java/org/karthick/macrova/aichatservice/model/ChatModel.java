package org.karthick.macrova.aichatservice.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatModel {

  private String model;
  private List<MessageModel> messages;
  private double temperature;
  private int max_tokens;
  private boolean stream;
}

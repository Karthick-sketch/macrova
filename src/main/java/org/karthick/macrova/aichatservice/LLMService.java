package org.karthick.macrova.aichatservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.karthick.macrova.aichatservice.model.ChatModel;

public interface LLMService {
  String chat(ChatModel chatModel) throws JsonProcessingException;

  String foodSuggestChat(String prompt) throws JsonProcessingException;
}

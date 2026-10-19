package org.karthick.macrova.aichatservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.karthick.macrova.aichatservice.model.ChatModel;
import org.karthick.macrova.aichatservice.model.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenRouterLLMService implements LLMService {

  private static final String URL =
    "https://openrouter.ai/api/v1/chat/completions";

  private final RestTemplate restTemplate;
  private final String model;
  private final String apiKey;

  public OpenRouterLLMService(
    RestTemplate restTemplate,
    @Value("${llm.openrouter.model}") String model,
    @Value("${llm.openrouter.api-key}") String apiKey
  ) {
    this.restTemplate = restTemplate;
    this.model = model;
    this.apiKey = apiKey;
  }

  @Override
  public String chat(ChatModel chatModel) throws JsonProcessingException {
    ResponseEntity<String> response = restTemplate.exchange(
      URL,
      HttpMethod.valueOf("POST"),
      getHttpEntity(chatModel),
      String.class
    );
    return getContent(response.getBody());
  }

  private HttpEntity<ChatModel> getHttpEntity(ChatModel chatModel) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return new HttpEntity<>(chatModel, headers);
  }

  private String getContent(String body) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.readTree(body);
    JsonNode contentNode = rootNode
      .path("choices")
      .path(0)
      .path("message")
      .path("content");
    return contentNode.asText();
  }

  @Override
  public String foodSuggestChat(String prompt) throws JsonProcessingException {
    return chat(buildFoodSuggestChatModel(prompt));
  }

  private ChatModel buildFoodSuggestChatModel(String prompt) {
    ChatModel chatModel = new ChatModel();
    chatModel.setModel(model);
    chatModel.setMessages(
      List.of(
        new MessageModel(
          "system",
          "Respond in JSON with this format: '{ name: string, calories: number, macronutrients: { protein: number, carbs: number, fat: number }, description: string }[]'. No other output. /no_think"
        ),
        new MessageModel("user", prompt)
      )
    );
    chatModel.setTemperature(0.3);
    chatModel.setMax_tokens(-1);
    chatModel.setStream(false);
    return chatModel;
  }
}

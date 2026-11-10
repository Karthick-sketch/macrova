package org.karthick.macrova.aifoodsuggest.entity;

import com.mongodb.lang.Nullable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "ai_food_suggest_responses")
public class AIFoodSuggestResponse {

  @Id
  private String id;

  private String prompt;

  @Nullable
  private String aiResponse;

  @Nullable
  private String errorResponse;

  @Nullable
  private List<String> aiFoodSuggestIds;

  public AIFoodSuggestResponse(String prompt) {
    this.prompt = prompt;
  }
}

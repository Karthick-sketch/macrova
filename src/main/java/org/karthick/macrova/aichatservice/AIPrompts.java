package org.karthick.macrova.aichatservice;

import org.karthick.macrova.aifoodsuggest.dto.FoodPreference;
import org.karthick.macrova.aifoodsuggest.dto.FoodPreferenceFilter;
import org.springframework.stereotype.Component;

@Component
public final class AIPrompts {

  private AIPrompts() {}

  private static final String FOOD_JSON_SCHEMA = """
    [
      {
        "name": "string",
        "description": "string",
        "calories": number,
        "macronutrients": {
          "protein": number,
          "carbs": number,
          "fat": number
        }
      }
    ]
    """;

  private static final String FOOD_SUGGEST_SYSTEM_PROMPT = """
    You are a nutrition assistant specializing in Indian cuisine.

    Generate realistic food suggestions based on the user's dietary preferences, nutritional goals and meal requirements.

    MEAL PREFERENCE GUIDELINES:
    - Balanced: Provide a balanced distribution of protein, carbohydrates and fat.
    - High Protein: Prioritize protein-rich foods.
    - Low Carb: Prioritize foods with lower carbohydrate content.
    - Low Fat: Prioritize foods with lower fat content.

    RULES:
    - Respect the selected cuisine and dietary preference.
    - Follow the selected meal preference.
    - Keep calories close to the requested target.
    - Provide realistic nutritional estimates.
    - Nutritional values must represent the entire food serving.
    - Avoid duplicate suggestions.
    - Return exactly the requested number of foods.

    OUTPUT FORMAT:
    %s

    OUTPUT RULES:
    - Return only a valid JSON array.
    - Include all required fields with correct data types.
    - Do not include additional fields.
    - Use numeric values for calories and macronutrients.
    - Do not include Markdown code fences or explanations.
    /no_think
    """.formatted(FOOD_JSON_SCHEMA);

  private static final String FOOD_SUGGEST_USER_PROMPT = """
    Suggest %d healthy %s cuisine %s foods for %s, with a %s meal preference, around %d calories per serving.

    Target macro distribution:
    Protein: %d%%
    Carbohydrates: %d%%
    Fat: %d%%

    Use the macro distribution as guidance when selecting foods.
    """;

  private static final String RETRY_SYSTEM_PROMPT = """
    You are a JSON repair assistant.

    Your task is to correct malformed JSON responses while preserving the original content.

    REPAIR RULES:
    - Correct JSON syntax errors.
    - Correct recognizable field name spelling mistakes.
    - Correct invalid JSON structure to match the schema.
    - Preserve all original food names and descriptions.
    - Preserve all original nutritional values.
    - Do not generate new food items.
    - Do not remove existing food items.
    - Do not invent missing information.
    - Do not modify existing values.
    - If a required value cannot be recovered unambiguously, return an empty JSON array.

    REQUIRED JSON STRUCTURE:
    %s

    OUTPUT RULES:
    - Return only valid JSON.
    - Include no additional fields.
    - Use the required field names and data types.
    - Do not include Markdown code fences or explanations.
    /no_think
    """.formatted(FOOD_JSON_SCHEMA);

  private static final String RETRY_USER_PROMPT = """
    The previous response contains invalid JSON.

    Repair the response according to the required JSON schema.
    Preserve all recoverable original food information and nutritional values without generating new content.

    INVALID RESPONSE:
    %s
    """;

  public static String getFoodSuggestSystemPrompt() {
    return FOOD_SUGGEST_SYSTEM_PROMPT;
  }

  public static String getFoodSuggestUserPrompt(FoodPreference preference) {
    FoodPreferenceFilter filter = preference.getFoodPreferenceFilter();
    return FOOD_SUGGEST_USER_PROMPT.formatted(
      preference.getSuggestCount(),
      filter.getCuisine(),
      filter.getMealType(),
      filter.getMeal(),
      filter.getDietType(),
      preference.getCalories(),
      preference.getProteinTarget(),
      preference.getCarbsTarget(),
      preference.getFatTarget()
    );
  }

  public static String getRetrySystemPrompt() {
    return RETRY_SYSTEM_PROMPT;
  }

  public static String getRetryUserPrompt(String invalidResponse) {
    return RETRY_USER_PROMPT.formatted(invalidResponse);
  }
}

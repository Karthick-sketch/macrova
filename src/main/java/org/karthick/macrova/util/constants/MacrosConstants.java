package org.karthick.macrova.util.constants;

public final class MacrosConstants {

  private MacrosConstants() {}

  public static final String BREAKFAST = "breakfast";
  public static final String LUNCH = "lunch";
  public static final String DINNER = "dinner";
  public static final String SNACK = "snack";

  public static boolean validateMacro(String macro) {
    return switch (macro) {
      case BREAKFAST, LUNCH, DINNER, SNACK -> true;
      default -> false;
    };
  }
}

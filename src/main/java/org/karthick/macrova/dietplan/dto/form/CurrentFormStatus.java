package org.karthick.macrova.dietplan.dto.form;

import lombok.Data;
import org.karthick.macrova.dietplan.enums.FormStatus;

@Data
public class CurrentFormStatus {

  private FormStatus formStatus;
}

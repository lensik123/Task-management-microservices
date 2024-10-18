package ru.baysarov.statistic.dto;

import java.util.Map;

public class ErrorResponse {

  private final String error;
  private final String message;
  private final Map<String, Object> details;

  public ErrorResponse(String error, String message, Map<String, Object> details) {
    this.error = error;
    this.message = message;
    this.details = details;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public Map<String, Object> getDetails() {
    return details;
  }
}

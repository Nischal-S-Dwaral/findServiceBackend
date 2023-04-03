package org.msc.web.dev.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.msc.web.dev.constants.Constants;

/**
 * ErrorResponse body for all exceptions
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

  private int status;
  private String url;
  private String message;
  private String description;
  private String returnCode;

  public static class Builder {

    private int status;
    private String url;
    private String message;
    private String description;

    public static Builder anError() {
      return new Builder();
    }

    public Builder withStatus(int status) {
      this.status = status;
      return this;
    }

    public Builder withUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public ErrorResponse build() {
      return new ErrorResponse(status, url, message, description, Constants.RETURN_CODE_ERROR);
    }
  }

}

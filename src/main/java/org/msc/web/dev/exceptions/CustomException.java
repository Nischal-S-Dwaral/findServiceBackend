package org.msc.web.dev.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomException extends RuntimeException {

  private static final long serialVersionUID = 8722516953922232439L;

  public CustomException(String message) {
    super(message);
  }

  public CustomException(String message, Throwable t) {
    super(message, t);
  }
}

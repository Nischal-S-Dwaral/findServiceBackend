package org.msc.web.dev.exceptions;

/**
 * Indicates an error during serialization due to misconfiguration or during deserialization due to
 * invalid input data.
 */
public class JsonException extends CustomException {

  public JsonException(String message) {
    super(message);
  }

}

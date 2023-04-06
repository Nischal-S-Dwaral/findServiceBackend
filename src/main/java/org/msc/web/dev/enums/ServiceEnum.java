package org.msc.web.dev.enums;

/**
 * @author : Nischal SD
 **/
public enum ServiceEnum {
  SERVICE("service"),
  SERVICE_PROVIDER("serviceProvider"),
  REVIEW("review"),
  SERVICE_REQUEST("serviceRequest");

  private final String name;

  ServiceEnum(String name) {
    this.name = name;
  }

  public static ServiceEnum findByModuleName(String modul) {

    for (ServiceEnum pm : values()) {
      if (pm.getName().equals(modul)) {
        return pm;
      }
    }
    return null;
  }

  public String getName() {
    return name;
  }

}

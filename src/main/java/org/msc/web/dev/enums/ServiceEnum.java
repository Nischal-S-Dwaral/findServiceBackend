package org.msc.web.dev.enums;

/**
 * @author : Nischal SD
 **/
public enum ServiceEnum {
  CUSTOMER("customer"),
  ADMIN("admin"),
  SERVICE_PROVIDER("serviceProvider"),
  REVIEW("review");

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

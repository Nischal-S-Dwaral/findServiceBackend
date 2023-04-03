package org.msc.web.dev.service;

import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.Forbidden;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UseCasesAdaptorFactory {

  private UseCasesAdaptorFactory() {}

  private static final Map<String, IUseCaseImplementation> useCaseImplementationMap = new HashMap<>();

  private static String getMapKey(String serviceCode, String useCase) {
    return
        String.format("%s:%s", serviceCode.toLowerCase(), useCase.toLowerCase());
  }

  public static IUseCaseImplementation getAdaptor(ServiceEnum serviceCode, UseCasesEnums useCase) {
    String key = getMapKey(serviceCode.getName(), useCase.getUseCaseName());
    if (useCaseImplementationMap.containsKey(key)) {
      return useCaseImplementationMap.get(key);
    }
    throw new BadRequest(
        "Provided useCase and serviceCode is not supported by the system.");
  }

  /**
   * Register the adaptor for handling particular use-cases. useCase code should be unique.
   *
   * @param useCase               use case must be unique and implementation must not exist.
   * @param useCaseImplementation use case implementation class.
   */
  public static void registerAdaptor(ServiceEnum serviceCode, UseCasesEnums useCase,
      IUseCaseImplementation useCaseImplementation) {
    String key = getMapKey(serviceCode.getName(), useCase.getUseCaseName());
    log.debug("Registered key:{} ,{} ", key, useCaseImplementation.getClass().getName());

    if (useCaseImplementationMap.containsKey(key)) {
      throw new Forbidden(" use case already implemented...");
    }
    useCaseImplementationMap.put(key, useCaseImplementation);
  }
}
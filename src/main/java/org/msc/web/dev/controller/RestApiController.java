package org.msc.web.dev.controller;

import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

@RestController
@Slf4j
public class RestApiController {

    @PostMapping("/api/{serviceCode}/{useCase}")
    @CrossOrigin("*")
    public <T extends RestApiResponse> T post(
            @PathVariable("serviceCode") String serviceCode,
            @PathVariable("useCase") String useCase,
            HttpServletRequest httpServletRequest) throws IOException {
        IUseCaseImplementation serviceAdaptor = UseCasesAdaptorFactory
                .getAdaptor(Objects.requireNonNull(ServiceEnum.findByModuleName(serviceCode)),
                        UseCasesEnums.getEnumByString(useCase));
        return (T) serviceAdaptor.execute(httpServletRequest);
    }

    @GetMapping("/api/{serviceCode}/{useCase}")
    @CrossOrigin("*")
    public <T extends RestApiResponse> T get(
            @PathVariable("serviceCode") String serviceCode,
            @PathVariable("useCase") String useCase,
            HttpServletRequest httpServletRequest) throws IOException {
        IUseCaseImplementation serviceAdaptor = UseCasesAdaptorFactory
                .getAdaptor(Objects.requireNonNull(ServiceEnum.findByModuleName(serviceCode)),
                        UseCasesEnums.getEnumByString(useCase));
        return (T) serviceAdaptor.execute(httpServletRequest);
    }

    @DeleteMapping("/api/{serviceCode}/{useCase}")
    @CrossOrigin("*")
    public <T extends RestApiResponse> T delete(
            @PathVariable("serviceCode") String serviceCode,
            @PathVariable("useCase") String useCase,
            HttpServletRequest httpServletRequest) throws IOException {
        IUseCaseImplementation serviceAdaptor = UseCasesAdaptorFactory
                .getAdaptor(Objects.requireNonNull(ServiceEnum.findByModuleName(serviceCode)),
                        UseCasesEnums.getEnumByString(useCase));
        return (T) serviceAdaptor.execute(httpServletRequest);
    }
}

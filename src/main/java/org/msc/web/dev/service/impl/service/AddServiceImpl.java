package org.msc.web.dev.service.impl.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.service.Position;
import org.msc.web.dev.model.service.add.AddServiceRequest;
import org.msc.web.dev.model.service.add.AddServiceResponse;
import org.msc.web.dev.repository.ServiceImageUploadRepository;
import org.msc.web.dev.repository.ServiceRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author nischalsd
 */
@Service
@Slf4j
public class AddServiceImpl implements IUseCaseImplementation<AddServiceRequest, WriteResult, AddServiceResponse> {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceImageUploadRepository serviceImageUploadRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE, UseCasesEnums.ADD, this);
    }

    @Override
    public AddServiceRequest preProcess(HttpServletRequest request) {
        AddServiceRequest addServiceRequest = new AddServiceRequest();
        MultiValueMap<String, MultipartFile> multipartFileMultiValueMap = ((StandardMultipartHttpServletRequest) request).getMultiFileMap();
        if (!multipartFileMultiValueMap.isEmpty()) {
            addServiceRequest.setPhotos(multipartFileMultiValueMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_PHOTOS));
        }
        addServiceRequest.setParameterMap(request.getParameterMap());

        return addServiceRequest;
    }

    @Override
    public WriteResult process(AddServiceRequest addServiceRequest) {
        try {
            List<String> photos = serviceImageUploadRepository.uploadServiceImageToFirebase(addServiceRequest.getPhotos());
            org.msc.web.dev.model.collections.Service service = generateServiceFromParameterMap(addServiceRequest.getParameterMap());
            service.setPhotos(photos);
            service.setCreatedAt(String.valueOf(new Timestamp(System.currentTimeMillis())));
            service.setNumberOfRatings(String.valueOf(0));
            service.setTotalRating(String.valueOf(0f));

            ApiFuture<WriteResult> data = serviceRepository.create(service);
            return data.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to save Service in FireBase "+exception.getMessage());
        }
    }

    @Override
    public AddServiceResponse postProcess(WriteResult writeResult) {
        return new AddServiceResponse(writeResult.getUpdateTime());
    }

    private org.msc.web.dev.model.collections.Service generateServiceFromParameterMap(Map<String, String[]> parameterMap) {

        List<String> missingParameters = new ArrayList<>();
        org.msc.web.dev.model.collections.Service service = new org.msc.web.dev.model.collections.Service();

        service.setPrice(parameterMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_PRICE)[0]);
        parameterMap.remove(ServiceConstants.MULTIPART_PARAMETER_KEY_PRICE);

        service.setAvailability(parameterMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_AVAILABILITY)[0]);
        parameterMap.remove(ServiceConstants.MULTIPART_PARAMETER_KEY_PRICE);

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {

            if (entry.getValue() != null) {
                switch (entry.getKey()) {
                    case ServiceConstants.MULTIPART_PARAMETER_KEY_CATEGORY: {
                        service.setCategory(parameterMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_CATEGORY)[0]);
                        break;
                    }
                    case ServiceConstants.MULTIPART_PARAMETER_KEY_SERVICE_PROVIDER_ID: {
                        service.setServiceProviderId(parameterMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_SERVICE_PROVIDER_ID)[0]);
                        break;
                    }
                    case ServiceConstants.MULTIPART_PARAMETER_KEY_LOCATION: {
                        service.setLocation(parameterMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_LOCATION)[0]);
                        break;
                    }
                    case ServiceConstants.MULTIPART_PARAMETER_KEY_DESCRIPTION: {
                        service.setDescription(parameterMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_DESCRIPTION)[0]);
                        break;
                    }
                    case ServiceConstants.MULTIPART_PARAMETER_KEY_TITLE: {
                        service.setName(parameterMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_TITLE)[0]);
                        break;
                    }
                    case ServiceConstants.MULTIPART_PARAMETER_KEY_POSITION: {
                        String position = parameterMap.get(ServiceConstants.MULTIPART_PARAMETER_KEY_POSITION)[0];
                        String[] splitPosition = position.split(",");

                        service.setPosition(new Position(
                                Double.parseDouble(splitPosition[0]),
                                Double.parseDouble(splitPosition[1])));
                        break;
                    }
                }
            } else {
                missingParameters.add(entry.getKey());
            }
        }

        if (!missingParameters.isEmpty()) {
            throw new BadRequest("These fields are missing in the request : " + missingParameters);
        }

        return service;
    }
}

package org.msc.web.dev.service.impl.servicerequest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.ServiceRequest;
import org.msc.web.dev.model.servicerequest.add.ServiceRequestAddRequest;
import org.msc.web.dev.model.servicerequest.add.ServiceRequestAddResponse;
import org.msc.web.dev.repository.ServiceRepository;
import org.msc.web.dev.repository.ServiceRequestRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.JsonUtil;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.ExecutionException;

@Service
public class AddServiceRequestImpl implements IUseCaseImplementation<
        ServiceRequestAddRequest, WriteResult, ServiceRequestAddResponse> {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_REQUEST, UseCasesEnums.ADD, this);
    }

    @Override
    public ServiceRequestAddRequest preProcess(HttpServletRequest request) throws IOException {
       ServiceRequestAddRequest serviceRequestAddRequest = RequestUtil.getRequestData(
               request,
               ServiceRequestAddRequest.class
       );
       if (CommonUtils.isAnyFieldEmpty(
               serviceRequestAddRequest.getClass().getDeclaredFields(), serviceRequestAddRequest)) {
           throw new BadRequest("All fields are necessary for adding the service request");
       }
       return serviceRequestAddRequest;
    }

    @Override
    public WriteResult process(ServiceRequestAddRequest serviceRequestAddRequest) {
        try {

            ServiceRequest serviceRequest = new ServiceRequest(serviceRequestAddRequest);

            org.msc.web.dev.model.collections.Service service = JsonUtil.toObject(
                    serviceRepository.findById(serviceRequestAddRequest.getServiceId()).get().getData(),
                    org.msc.web.dev.model.collections.Service.class);

            serviceRequest.setServiceProviderId(service.getServiceProviderId());
            serviceRequest.setServiceName(service.getName());
            serviceRequest.setServiceCategory(service.getCategory());
            serviceRequest.setPrice(service.getPrice());
            serviceRequest.setCreatedAt(String.valueOf(new Timestamp(System.currentTimeMillis())));
            serviceRequest.setStatus(ServiceRequestConstants.SERVICE_REQUEST_STATUS_PENDING);

            ApiFuture<WriteResult> data = serviceRequestRepository.create(serviceRequest);
            return data.get();

        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to save Service Request in FireBase "+exception.getMessage());
        }
    }

    @Override
    public ServiceRequestAddResponse postProcess(WriteResult writeResult) {
        return new ServiceRequestAddResponse(writeResult.getUpdateTime());
    }
}

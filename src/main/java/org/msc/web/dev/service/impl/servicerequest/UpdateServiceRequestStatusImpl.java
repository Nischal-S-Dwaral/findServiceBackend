package org.msc.web.dev.service.impl.servicerequest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.servicerequest.update.ServiceRequestStatusUpdateRequest;
import org.msc.web.dev.model.servicerequest.update.ServiceRequestStatusUpdateResponse;
import org.msc.web.dev.repository.NotificationRepository;
import org.msc.web.dev.repository.ServiceRequestRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UpdateServiceRequestStatusImpl implements IUseCaseImplementation<
        ServiceRequestStatusUpdateRequest, WriteResult, ServiceRequestStatusUpdateResponse> {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_REQUEST, UseCasesEnums.UPDATE_STATUS, this);
    }

    @Override
    public ServiceRequestStatusUpdateRequest preProcess(HttpServletRequest request) throws IOException {
        ServiceRequestStatusUpdateRequest statusUpdateRequest = RequestUtil.getRequestData(
                request,
                ServiceRequestStatusUpdateRequest.class
        );
        if (CommonUtils.isAnyFieldEmpty(statusUpdateRequest.getClass().getDeclaredFields(), statusUpdateRequest)) {
            throw new BadRequest("All fields are required to serve the data.");
        }
        return statusUpdateRequest;
    }

    @Override
    public WriteResult process(ServiceRequestStatusUpdateRequest serviceRequestStatusUpdateRequest) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put(ServiceRequestConstants.FIELD_STATUS, serviceRequestStatusUpdateRequest.getUpdatedStatus());

        try {
            ApiFuture<WriteResult> data = serviceRequestRepository.updateStatus(
                    serviceRequestStatusUpdateRequest.getServiceRequestId(), fieldMap
            );
            if (serviceRequestStatusUpdateRequest.getUpdatedStatus().equals("Completed")) {
                notificationRepository.createForReview(serviceRequestStatusUpdateRequest.getServiceRequestId());
            } else {
                notificationRepository.createForUpdateByServiceProvider(
                        serviceRequestStatusUpdateRequest.getServiceRequestId(),
                        "Updated the status to "+ serviceRequestStatusUpdateRequest.getUpdatedStatus()+" for the request: "+ serviceRequestStatusUpdateRequest.getServiceRequestId()
                );
            }
            return data.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to update Service Request Status in FireBase "+exception.getMessage());
        }
    }

    @Override
    public ServiceRequestStatusUpdateResponse postProcess(WriteResult writeResult) {
        return new ServiceRequestStatusUpdateResponse(writeResult.getUpdateTime());
    }
}

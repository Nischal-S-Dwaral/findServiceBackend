package org.msc.web.dev.service.impl.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.service.update.UpdateServiceRequest;
import org.msc.web.dev.model.service.update.UpdateServiceResponse;
import org.msc.web.dev.repository.NotificationRepository;
import org.msc.web.dev.repository.ServiceRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class UpdateServiceImpl implements IUseCaseImplementation<
        UpdateServiceRequest, WriteResult, UpdateServiceResponse> {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE, UseCasesEnums.UPDATE_SERVICE, this);
    }

    @Override
    public UpdateServiceRequest preProcess(HttpServletRequest request) throws IOException {
        UpdateServiceRequest updateServiceRequest = RequestUtil.getRequestData(
                request,
                UpdateServiceRequest.class
        );
        if (CommonUtils.isAnyFieldEmpty(updateServiceRequest.getClass().getDeclaredFields(), updateServiceRequest)) {
            throw new BadRequest("All fields are required to serve the data.");
        }
        return updateServiceRequest;
    }

    @Override
    public WriteResult process(UpdateServiceRequest updateServiceRequest) {
        try {
            ApiFuture<WriteResult> writeResult = serviceRepository.updateService(updateServiceRequest);
            notificationRepository.createForUpdateInService(updateServiceRequest.getServiceId());
            return writeResult.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to update Service in FireBase "+exception.getMessage());
        }
    }

    @Override
    public UpdateServiceResponse postProcess(WriteResult writeResult) {
        return new UpdateServiceResponse(writeResult.getUpdateTime());
    }
}

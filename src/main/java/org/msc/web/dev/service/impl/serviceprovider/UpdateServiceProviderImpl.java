package org.msc.web.dev.service.impl.serviceprovider;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.serviceprovider.update.UpdateStatusServiceProviderRequest;
import org.msc.web.dev.model.serviceprovider.update.UpdateStatusServiceProviderResponse;
import org.msc.web.dev.repository.ServiceProviderRepository;
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
@Slf4j
public class UpdateServiceProviderImpl implements IUseCaseImplementation<
        UpdateStatusServiceProviderRequest, WriteResult, UpdateStatusServiceProviderResponse> {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_PROVIDER, UseCasesEnums.UPDATE_STATUS, this);
    }

    @Override
    public UpdateStatusServiceProviderRequest preProcess(HttpServletRequest request) throws IOException {
        UpdateStatusServiceProviderRequest updateStatusServiceProviderRequest = RequestUtil.getRequestData(
                request,
                UpdateStatusServiceProviderRequest.class
        );
        if (CommonUtils.isAnyFieldEmpty(updateStatusServiceProviderRequest.getClass().getDeclaredFields(), updateStatusServiceProviderRequest)) {
            throw new BadRequest("All fields are required to serve the data.");
        }
        return updateStatusServiceProviderRequest;
    }

    @Override
    public WriteResult process(UpdateStatusServiceProviderRequest updateStatusServiceProviderRequest) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("approvalStatus", updateStatusServiceProviderRequest.getUpdateStatus());

        try {
            ApiFuture<WriteResult> data = serviceProviderRepository.updateStatus(
                    updateStatusServiceProviderRequest.getServiceProviderId(), fieldMap
            );
            return data.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to update Service Provider in FireBase "+exception.getMessage());
        }
    }

    @Override
    public UpdateStatusServiceProviderResponse postProcess(WriteResult writeResult) {
        return new UpdateStatusServiceProviderResponse(writeResult.getUpdateTime());
    }
}

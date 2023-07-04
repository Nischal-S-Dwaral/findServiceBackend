package org.msc.web.dev.service.impl.serviceprovider;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceProviderConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.serviceprovider.delete.DeleteServiceProviderResponse;
import org.msc.web.dev.repository.ServiceProviderRepository;
import org.msc.web.dev.repository.ServiceRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class DeleteServiceProviderImpl implements IUseCaseImplementation<
        String, WriteResult, DeleteServiceProviderResponse> {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_PROVIDER, UseCasesEnums.DELETE, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceProviderConstants.HEADER_ID))) {
            throw new BadRequest("ID is required get find");
        }
        return queryParams.get(ServiceProviderConstants.HEADER_ID);
    }

    @Override
    public WriteResult process(String id) {
        try {
            ApiFuture<WriteResult> writeResultApiFuture = serviceProviderRepository.delete(id);
            serviceRepository.deleteByServiceProvider(id);
            return writeResultApiFuture.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to delete Service Provider from FireBase "+exception.getMessage());
        }
    }

    @Override
    public DeleteServiceProviderResponse postProcess(WriteResult writeResult) {
        return new DeleteServiceProviderResponse(writeResult.getUpdateTime());
    }
}

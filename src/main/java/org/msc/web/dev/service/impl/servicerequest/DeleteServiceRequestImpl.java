package org.msc.web.dev.service.impl.servicerequest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.constants.ServiceProviderConstants;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.servicerequest.delete.DeleteServiceRequestResponse;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class DeleteServiceRequestImpl implements IUseCaseImplementation<
        String, WriteResult, DeleteServiceRequestResponse> {

    @Autowired
    private ServiceRequestRepository repository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_REQUEST, UseCasesEnums.DELETE, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceRequestConstants.HEADER_ID))) {
            throw new BadRequest("ID is required get find");
        }
        return queryParams.get(ServiceProviderConstants.HEADER_ID);
    }

    @Override
    public WriteResult process(String id) {
        try {
            ApiFuture<WriteResult> writeResultApiFuture = repository.delete(id);
            return writeResultApiFuture.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to delete Service Request from FireBase "+exception.getMessage());
        }
    }

    @Override
    public DeleteServiceRequestResponse postProcess(WriteResult writeResult) {
        return new DeleteServiceRequestResponse(writeResult.getUpdateTime());
    }
}

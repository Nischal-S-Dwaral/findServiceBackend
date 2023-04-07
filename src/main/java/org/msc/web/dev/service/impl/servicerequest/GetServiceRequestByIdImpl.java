package org.msc.web.dev.service.impl.servicerequest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.ServiceRequest;
import org.msc.web.dev.model.servicerequest.get.GetServiceRequestByIdResponse;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class GetServiceRequestByIdImpl implements IUseCaseImplementation<
        String, DocumentSnapshot, GetServiceRequestByIdResponse> {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_REQUEST, UseCasesEnums.GET_BY_ID, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceRequestConstants.HEADER_ID))) {
            throw new BadRequest("ID is required to find");
        }
        return queryParams.get(ServiceRequestConstants.HEADER_ID);
    }

    @Override
    public DocumentSnapshot process(String requestId) {
        try {
            ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = serviceRequestRepository.findById(requestId);
            return documentSnapshotApiFuture.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get Service Request with request ID from FireBase "+exception.getMessage());
        }
    }

    @Override
    public GetServiceRequestByIdResponse postProcess(DocumentSnapshot documentSnapshot) {
        Map<String, Object> documentMap = documentSnapshot.getData();
        return new GetServiceRequestByIdResponse(JsonUtil.toObject(documentMap, ServiceRequest.class));
    }
}

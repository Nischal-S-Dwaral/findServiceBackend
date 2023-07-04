package org.msc.web.dev.service.impl.servicerequest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.ServiceRequest;
import org.msc.web.dev.model.servicerequest.get.GetServiceRequestCustomQueryRequest;
import org.msc.web.dev.model.servicerequest.get.GetServiceRequestCustomQueryResponse;
import org.msc.web.dev.repository.ServiceRequestRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class GetServiceRequestCustomQueryImpl implements IUseCaseImplementation<
        GetServiceRequestCustomQueryRequest, List<QueryDocumentSnapshot>, GetServiceRequestCustomQueryResponse> {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_REQUEST, UseCasesEnums.GET_SERVICE_REQUEST_LIST, this);
    }

    @Override
    public GetServiceRequestCustomQueryRequest preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        String parameter = null;

        if (CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceRequestConstants.HEADER_SERVICE_PROVIDER_ID))) {
            parameter = ServiceRequestConstants.HEADER_SERVICE_PROVIDER_ID;
        } else if (CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceRequestConstants.HEADER_CUSTOMER_ID))) {
            parameter = ServiceRequestConstants.HEADER_CUSTOMER_ID;
        } else if (CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceRequestConstants.HEADER_SERVICE_ID))) {
            parameter = ServiceRequestConstants.HEADER_SERVICE_ID;
        }

        if (!CommonUtils.checkIfObjectIsNotNull(parameter)) {
            throw new BadRequest("Correct ID parameter is required get find");
        } else {
            return new GetServiceRequestCustomQueryRequest(queryParams.get(parameter), parameter);
        }
    }

    @Override
    public List<QueryDocumentSnapshot> process(GetServiceRequestCustomQueryRequest getServiceRequestCustomQueryRequest) {
        try {
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = serviceRequestRepository.getServiceRequestList(
                    getServiceRequestCustomQueryRequest.getField(),
                    getServiceRequestCustomQueryRequest.getRequestId());
            return querySnapshotApiFuture.get().getDocuments();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get review list from FireBase for request id- "+getServiceRequestCustomQueryRequest.getRequestId()+ " :"+exception.getMessage());
        }
    }

    @Override
    public GetServiceRequestCustomQueryResponse postProcess(List<QueryDocumentSnapshot> queryDocumentSnapshotList) {

        List<ServiceRequest> serviceRequestList = queryDocumentSnapshotList.stream()
                .map(d -> d.toObject(ServiceRequest.class))
                .collect(Collectors.toList());

        return new GetServiceRequestCustomQueryResponse(serviceRequestList);
    }
}
package org.msc.web.dev.service.impl.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.service.get.GetServiceByIdResponse;
import org.msc.web.dev.repository.ServiceRepository;
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
@Slf4j
public class GetServiceByIdImpl implements IUseCaseImplementation<
        String, DocumentSnapshot, GetServiceByIdResponse> {

    @Autowired
    private ServiceRepository serviceRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE, UseCasesEnums.FIND_BY_ID, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceConstants.HEADER_ID))) {
            throw new BadRequest("ID is required to find");
        }
        return queryParams.get(ServiceConstants.HEADER_ID);
    }

    @Override
    public DocumentSnapshot process(String serviceId) {
        try {
            ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = serviceRepository.findById(serviceId);
            return documentSnapshotApiFuture.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get Service with serviceId from FireBase "+exception.getMessage());
        }
    }

    @Override
    public GetServiceByIdResponse postProcess(DocumentSnapshot documentSnapshot) {
        Map<String, Object> documentMap = documentSnapshot.getData();
        return new GetServiceByIdResponse(JsonUtil.toObject(documentMap, org.msc.web.dev.model.collections.Service.class));
    }
}

package org.msc.web.dev.service.impl.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.service.get.GetServiceListResponse;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetServiceListImpl implements IUseCaseImplementation<
        String, List<org.msc.web.dev.model.collections.Service>, GetServiceListResponse> {

    @Autowired
    private ServiceRepository serviceRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE, UseCasesEnums.GET_SERVICE_LIST, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        return queryParams.get(ServiceConstants.HEADER_CATEGORY);
    }

    @Override
    public List<org.msc.web.dev.model.collections.Service> process(String parameter) {
        try {
            ApiFuture<QuerySnapshot> querySnapshotApiFuture;
            if (CommonUtils.checkIfObjectIsNotNull(parameter)) {
                querySnapshotApiFuture = serviceRepository.getServiceListFromCategory(parameter);
            } else {
                querySnapshotApiFuture = serviceRepository.getAllServiceList();
            }
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();
            return queryDocumentSnapshotList.stream()
                    .map(d -> d.toObject(org.msc.web.dev.model.collections.Service.class))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get Service List from FireBase "+exception.getMessage());
        }
    }

    @Override
    public GetServiceListResponse postProcess(List<org.msc.web.dev.model.collections.Service> services) {
        return new GetServiceListResponse(services);
    }
}

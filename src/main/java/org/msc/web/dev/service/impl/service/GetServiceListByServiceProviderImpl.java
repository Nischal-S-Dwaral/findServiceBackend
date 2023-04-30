package org.msc.web.dev.service.impl.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.ServiceProvider;
import org.msc.web.dev.model.service.get.GetServiceListByServiceProviderData;
import org.msc.web.dev.model.service.get.GetServiceListByServiceProviderResponse;
import org.msc.web.dev.repository.ServiceProviderRepository;
import org.msc.web.dev.repository.ServiceRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetServiceListByServiceProviderImpl implements IUseCaseImplementation<
        String, GetServiceListByServiceProviderData, GetServiceListByServiceProviderResponse> {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE, UseCasesEnums.GET_SERVICE_LIST_BY_SERVICE_PROVIDER, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        return queryParams.get(ServiceConstants.HEADER_SERVICE_PROVIDER_ID);
    }

    @Override
    public GetServiceListByServiceProviderData process(String serviceProviderId) {
        try {
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = serviceRepository.getByServiceProvider(serviceProviderId);
            ApiFuture<QuerySnapshot>  querySnapshotApiFuture1 = serviceProviderRepository.findBySPid(serviceProviderId);
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();

            List<org.msc.web.dev.model.collections.Service> serviceList = queryDocumentSnapshotList.stream()
                    .map(d -> d.toObject(org.msc.web.dev.model.collections.Service.class))
                    .collect(Collectors.toList());

            return new GetServiceListByServiceProviderData(serviceList, querySnapshotApiFuture1.get());
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get Service List from FireBase "+exception.getMessage());
        }
    }

    @Override
    public GetServiceListByServiceProviderResponse postProcess(
            GetServiceListByServiceProviderData getServiceListByServiceProviderData) {

        ServiceProvider serviceProvider = getServiceListByServiceProviderData.getQuerySnapshot()
                .getDocuments().get(0).toObject(ServiceProvider.class);

        return new GetServiceListByServiceProviderResponse(
                getServiceListByServiceProviderData.getServiceList(),
                serviceProvider.getName()
        );
    }
}

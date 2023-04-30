package org.msc.web.dev.service.impl.serviceprovider;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.msc.web.dev.constants.ServiceProviderConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.ServiceProvider;
import org.msc.web.dev.model.serviceprovider.get.GetServiceProviderListByStatusResponse;
import org.msc.web.dev.repository.ServiceProviderRepository;
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
public class GetServiceProviderListByStatusImpl implements IUseCaseImplementation<
        String, List<ServiceProvider>, GetServiceProviderListByStatusResponse> {

    @Autowired
    private ServiceProviderRepository repository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_PROVIDER, UseCasesEnums.GET_SERVICE_PROVIDER_LIST, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        return queryParams.get(ServiceProviderConstants.HEADER_APPROVAL_STATUS);
    }

    @Override
    public List<ServiceProvider> process(String status) {
        try {
            String formattedStatus = status.replaceAll("%20", " ");
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = repository.getServiceProviderListByStatus(formattedStatus);
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();
            return queryDocumentSnapshotList.stream()
                    .map(d -> d.toObject(ServiceProvider.class))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get Service Provider List from FireBase "+exception.getMessage());
        }
    }

    @Override
    public GetServiceProviderListByStatusResponse postProcess(List<ServiceProvider> serviceProviders) {
        return new GetServiceProviderListByStatusResponse(serviceProviders);
    }
}

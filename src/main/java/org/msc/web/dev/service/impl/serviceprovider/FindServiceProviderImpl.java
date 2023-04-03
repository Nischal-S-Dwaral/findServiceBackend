package org.msc.web.dev.service.impl.serviceprovider;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceProviderConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.ServiceProvider;
import org.msc.web.dev.model.serviceprovider.find.FindServiceProviderResponse;
import org.msc.web.dev.repository.ServiceProviderRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.JsonUtil;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FindServiceProviderImpl  implements IUseCaseImplementation<
        String, DocumentSnapshot, FindServiceProviderResponse> {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_PROVIDER, UseCasesEnums.FIND_BY_ID, this);
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
    public DocumentSnapshot process(String id) {
        try {
            ApiFuture<DocumentSnapshot>  documentSnapshotApiFuture = serviceProviderRepository.findById(id);
            return documentSnapshotApiFuture.get();
        } catch (ExecutionException|InterruptedException exception) {
            throw new InternalServerError("Failed to get Service Provider from FireBase "+exception.getMessage());
        }
    }

    @Override
    public FindServiceProviderResponse postProcess(DocumentSnapshot documentSnapshot) {
        Map<String, Object> documentMap = documentSnapshot.getData();
        return new FindServiceProviderResponse(JsonUtil.toObject(documentMap, ServiceProvider.class));
    }
}

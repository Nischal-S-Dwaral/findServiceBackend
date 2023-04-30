package org.msc.web.dev.service.impl.serviceprovider;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceProviderConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.ServiceProvider;
import org.msc.web.dev.model.serviceprovider.create.CreateServiceProviderRequest;
import org.msc.web.dev.model.serviceprovider.create.CreateServiceProviderResponse;
import org.msc.web.dev.repository.ServiceProviderRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class CreateServiceProviderImpl implements IUseCaseImplementation<
        CreateServiceProviderRequest, WriteResult, CreateServiceProviderResponse> {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE_PROVIDER, UseCasesEnums.CREATE, this);
    }

    /**
     * @implNote From the request we collect the data required for the API
     */
    @Override
    public CreateServiceProviderRequest preProcess(HttpServletRequest request) {
        CreateServiceProviderRequest createServiceProviderRequest = RequestUtil.getRequestData(
                request,
                CreateServiceProviderRequest.class
        );
        if (CommonUtils.isAnyFieldEmpty(createServiceProviderRequest.getClass().getDeclaredFields(), createServiceProviderRequest)) {
            throw new BadRequest("All fields are required to serve the data.");
        }
        return createServiceProviderRequest;
    }

    /**
     * @implNote Query the database and get the data required for the API
     */
    @Override
    public WriteResult process(CreateServiceProviderRequest request) {

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setEmail(request.getEmail());
        serviceProvider.setAddress(request.getAddress());
        serviceProvider.setName(request.getName());
        serviceProvider.setDescription(request.getDescription());
        serviceProvider.setId(request.getId());
        System.out.println(request.getId());
        serviceProvider.setApprovalStatus(ServiceProviderConstants.INITIAL_SERVICE_PROVIDER_REQUEST);

        try {
            ApiFuture<WriteResult> data = serviceProviderRepository.create(serviceProvider);
            return data.get();
        } catch (ExecutionException|InterruptedException exception) {
            throw new InternalServerError("Failed to save Service Provider in FireBase "+exception.getMessage());
        }
    }

    /**
     * @implNote Use the database data and send the response in the format needed for the UI
     */
    @Override
    public CreateServiceProviderResponse postProcess(WriteResult writeResult) {
        return new CreateServiceProviderResponse(writeResult.getUpdateTime());
    }
}

package org.msc.web.dev.service.impl.service;

import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.msc.web.dev.constants.ServiceConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.model.service.delete.DeleteServiceResponse;
import org.msc.web.dev.repository.ServiceRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DeleteServiceBasedOnServiceProviderImpl implements IUseCaseImplementation<
        String, List<WriteResult>, DeleteServiceResponse> {

    @Autowired
    private ServiceRepository serviceRepository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SERVICE, UseCasesEnums.DELETE_BY_SERVICE_PROVIDER,this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceConstants.HEADER_SERVICE_PROVIDER_ID))) {
            throw new BadRequest("ID is required to delete");
        }
        return queryParams.get(ServiceConstants.HEADER_SERVICE_PROVIDER_ID);
    }

    @Override
    public List<WriteResult> process(String serviceProviderId) {
        return serviceRepository.deleteByServiceProvider(serviceProviderId);
    }

    @Override
    public DeleteServiceResponse postProcess(List<WriteResult> writeResultList) {
        return new DeleteServiceResponse(writeResultList.size());
    }
}

package org.msc.web.dev.service.impl.comments.serviceprovider;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.CommentsServiceProviderRequest;
import org.msc.web.dev.model.comments.serviceprovider.add.CommentsServiceProviderAddRequest;
import org.msc.web.dev.model.comments.serviceprovider.add.CommentsServiceProviderAddResponse;
import org.msc.web.dev.repository.CommentsServiceProviderRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class AddCommentsServiceProviderImpl implements IUseCaseImplementation<
        CommentsServiceProviderAddRequest, WriteResult, CommentsServiceProviderAddResponse> {

    @Autowired
    private CommentsServiceProviderRepository repository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.COMMENT_SERVICE_PROVIDER, UseCasesEnums.ADD, this);
    }

    @Override
    public CommentsServiceProviderAddRequest preProcess(HttpServletRequest request) throws IOException {
        CommentsServiceProviderAddRequest addRequest = RequestUtil.getRequestData(
                request,
                CommentsServiceProviderAddRequest.class
        );
        if (CommonUtils.isAnyFieldEmpty(addRequest.getClass().getDeclaredFields(), addRequest)) {
            throw new BadRequest("All fields are required to serve the data.");
        }
        return addRequest;
    }

    @Override
    public WriteResult process(CommentsServiceProviderAddRequest addRequest) {

        CommentsServiceProviderRequest commentsServiceProviderRequest = new CommentsServiceProviderRequest(addRequest);

        try {
            ApiFuture<WriteResult> writeResultApiFuture = repository.add(commentsServiceProviderRequest);
            return writeResultApiFuture.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to save CommentsServiceProviderRequest in FireBase "+exception.getMessage());
        }
    }

    @Override
    public CommentsServiceProviderAddResponse postProcess(WriteResult writeResult) {
        return new CommentsServiceProviderAddResponse(writeResult.getUpdateTime());
    }
}

package org.msc.web.dev.service.impl.comments.serviceprovider;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.CommentsServiceProviderRequest;
import org.msc.web.dev.model.comments.serviceprovider.get.CommentsServiceProviderGetResponse;
import org.msc.web.dev.repository.CommentsServiceProviderRepository;
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
public class GetCommentsServiceProviderImpl implements IUseCaseImplementation<
        String, List<QueryDocumentSnapshot>, CommentsServiceProviderGetResponse> {

    @Autowired
    private CommentsServiceProviderRepository repository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.COMMENT_SERVICE_PROVIDER, UseCasesEnums.GET_BY_ID, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceRequestConstants.HEADER_SERVICE_PROVIDER_ID))) {
            throw new BadRequest("ID is required get comment list");
        }
        return queryParams.get(ServiceRequestConstants.HEADER_SERVICE_PROVIDER_ID);
    }

    @Override
    public List<QueryDocumentSnapshot> process(String serviceProviderId) {
        try {
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = repository.getCommentList(serviceProviderId);
            return querySnapshotApiFuture.get().getDocuments();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get comment list from FireBase for service id- "+serviceProviderId+ " :"+exception.getMessage());
        }
    }

    @Override
    public CommentsServiceProviderGetResponse postProcess(List<QueryDocumentSnapshot> queryDocumentSnapshotList) {
        List<CommentsServiceProviderRequest> commentsServiceProviderRequests = queryDocumentSnapshotList.stream()
                .map(d -> d.toObject(CommentsServiceProviderRequest.class))
                .collect(Collectors.toList());
        return new CommentsServiceProviderGetResponse(commentsServiceProviderRequests);
    }
}

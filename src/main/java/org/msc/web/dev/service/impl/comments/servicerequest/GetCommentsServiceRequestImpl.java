package org.msc.web.dev.service.impl.comments.servicerequest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.CommentsServiceRequest;
import org.msc.web.dev.model.comments.servicerequest.get.CommentsServiceRequestGetResponse;
import org.msc.web.dev.repository.CommentsServiceRequestRepository;
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
public class GetCommentsServiceRequestImpl implements IUseCaseImplementation<
        String, List<QueryDocumentSnapshot>, CommentsServiceRequestGetResponse> {

    @Autowired
    private CommentsServiceRequestRepository repository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.COMMENT_SERVICE_REQUEST, UseCasesEnums.GET_BY_ID, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ServiceRequestConstants.HEADER_SERVICE_REQUEST_ID))) {
            throw new BadRequest("ID is required get comment list");
        }
        return queryParams.get(ServiceRequestConstants.HEADER_SERVICE_REQUEST_ID);
    }

    @Override
    public List<QueryDocumentSnapshot> process(String serviceRequestId) {
        try {
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = repository.getCommentList(serviceRequestId);
            return querySnapshotApiFuture.get().getDocuments();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get comment list from FireBase for service id- "+serviceRequestId+ " :"+exception.getMessage());
        }
    }

    @Override
    public CommentsServiceRequestGetResponse postProcess(List<QueryDocumentSnapshot> queryDocumentSnapshotList) {
        List<CommentsServiceRequest> commentsServiceRequestList = queryDocumentSnapshotList.stream()
                .map(d -> d.toObject(CommentsServiceRequest.class))
                .collect(Collectors.toList());
        return new CommentsServiceRequestGetResponse(commentsServiceRequestList);
    }
}

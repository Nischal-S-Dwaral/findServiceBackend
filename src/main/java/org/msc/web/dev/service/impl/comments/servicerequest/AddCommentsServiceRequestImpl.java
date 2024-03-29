package org.msc.web.dev.service.impl.comments.servicerequest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.CommentsServiceRequest;
import org.msc.web.dev.model.comments.servicerequest.add.CommentsServiceRequestAddRequest;
import org.msc.web.dev.model.comments.servicerequest.add.CommentsServiceRequestAddResponse;
import org.msc.web.dev.repository.CommentsServiceRequestRepository;
import org.msc.web.dev.repository.NotificationRepository;
import org.msc.web.dev.repository.ServiceRequestRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class AddCommentsServiceRequestImpl implements IUseCaseImplementation<
        CommentsServiceRequestAddRequest, WriteResult, CommentsServiceRequestAddResponse> {

    @Autowired
    private CommentsServiceRequestRepository repository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.COMMENT_SERVICE_REQUEST, UseCasesEnums.ADD, this);
    }

    @Override
    public CommentsServiceRequestAddRequest preProcess(HttpServletRequest request) throws IOException {
        CommentsServiceRequestAddRequest addRequest = RequestUtil.getRequestData(
                request,
                CommentsServiceRequestAddRequest.class
        );
        if (CommonUtils.isAnyFieldEmpty(addRequest.getClass().getDeclaredFields(), addRequest)) {
            throw new BadRequest("All fields are required to serve the data.");
        }
        return addRequest;
    }

    @Override
    public WriteResult process(CommentsServiceRequestAddRequest commentsServiceRequestAddRequest) {

        CommentsServiceRequest commentsServiceRequest = new CommentsServiceRequest(commentsServiceRequestAddRequest);

        try {
            ApiFuture<WriteResult> writeResultApiFuture = repository.add(commentsServiceRequest);
            if (commentsServiceRequestAddRequest.isFromServiceProvider()) {
                notificationRepository.createForUpdateByServiceProvider(
                        commentsServiceRequestAddRequest.getServiceRequestId(),
                        "Comment added by in Service Request - " + commentsServiceRequestAddRequest.getServiceRequestId()
                );

                Map<String, Object> fieldMap = new HashMap<>();
                fieldMap.put(ServiceRequestConstants.FIELD_STATUS, "Update Required");

                serviceRequestRepository.updateStatus(
                        commentsServiceRequestAddRequest.getServiceRequestId(), fieldMap
                );
            }
            return writeResultApiFuture.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to save CommentsServiceRequest in FireBase "+exception.getMessage());
        }
    }

    @Override
    public CommentsServiceRequestAddResponse postProcess(WriteResult writeResult) {
        return new CommentsServiceRequestAddResponse(writeResult.getUpdateTime());
    }
}

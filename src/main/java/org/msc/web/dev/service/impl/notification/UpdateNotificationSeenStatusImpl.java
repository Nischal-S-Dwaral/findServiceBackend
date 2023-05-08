package org.msc.web.dev.service.impl.notification;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.notification.update.UpdateNotificationSeenStatusRequest;
import org.msc.web.dev.model.notification.update.UpdateNotificationSeenStatusResponse;
import org.msc.web.dev.repository.NotificationRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UpdateNotificationSeenStatusImpl implements IUseCaseImplementation<
        UpdateNotificationSeenStatusRequest, WriteResult, UpdateNotificationSeenStatusResponse> {

    @Autowired
    private NotificationRepository notificationRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.NOTIFICATION, UseCasesEnums.UPDATE_STATUS, this);
    }

    @Override
    public UpdateNotificationSeenStatusRequest preProcess(HttpServletRequest request) throws IOException {
        UpdateNotificationSeenStatusRequest updateNotificationSeenStatusRequest = RequestUtil.getRequestData(
                request,
                UpdateNotificationSeenStatusRequest.class
        );
        if (CommonUtils.isAnyFieldEmpty(updateNotificationSeenStatusRequest.getClass().getDeclaredFields(), updateNotificationSeenStatusRequest)) {
            throw new BadRequest("All fields are required to serve the data.");
        }
        return updateNotificationSeenStatusRequest;
    }

    @Override
    public WriteResult process(UpdateNotificationSeenStatusRequest updateNotificationSeenStatusRequest) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("seen", updateNotificationSeenStatusRequest.isSeen());

        try {
            ApiFuture<WriteResult> data = notificationRepository.updateSeenStatus(
                    updateNotificationSeenStatusRequest.getNotificationId(), fieldMap
            );
            return data.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to update Notification in FireBase "+exception.getMessage());
        }
    }

    @Override
    public UpdateNotificationSeenStatusResponse postProcess(WriteResult writeResult) {
        return new UpdateNotificationSeenStatusResponse(writeResult.getUpdateTime());
    }
}

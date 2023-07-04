package org.msc.web.dev.service.impl.notification;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import jakarta.servlet.http.HttpServletRequest;
import org.msc.web.dev.constants.NotificationConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.Notification;
import org.msc.web.dev.model.notification.get.GetAllNotificationsResponse;
import org.msc.web.dev.repository.NotificationRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class GetAllNotificationsImpl implements IUseCaseImplementation<
        String, List<Notification>, GetAllNotificationsResponse> {

    @Autowired
    private NotificationRepository repository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.registerAdaptor(
                ServiceEnum.NOTIFICATION, UseCasesEnums.GET_BY_ID, this
        );
    }

    @Override
    public String preProcess(HttpServletRequest request) throws IOException {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        return queryParams.get("customerId");
    }

    @Override
    public List<Notification> process(String customerId) {
        try {
            ApiFuture<QuerySnapshot> notificationQuerySnapshot = repository.geAllNotifications(customerId);
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = notificationQuerySnapshot.get().getDocuments();

            return queryDocumentSnapshotList.stream()
                    .map(d -> d.toObject(Notification.class))
                    .collect(Collectors.toList());

        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get Notification List from FireBase "+exception.getMessage());
        }
    }

    @Override
    public GetAllNotificationsResponse postProcess(List<Notification> notifications) {

        List<Notification> generalNotifications = notifications.stream()
                .filter(item -> NotificationConstants.TYPE_GENERAL.equals(item.getType()))
                .collect(Collectors.toList());

        List<Notification> serviceRequestUpdates = notifications.stream()
                .filter(item -> NotificationConstants.TYPE_UPDATE_SERVICE_REQUEST.equals(item.getType()))
                .collect(Collectors.toList());

        List<Notification> reviewRequest = notifications.stream()
                .filter(item -> NotificationConstants.TYPE_REVIEW.equals(item.getType()))
                .collect(Collectors.toList());

        return new GetAllNotificationsResponse(serviceRequestUpdates, reviewRequest, generalNotifications);
    }
}

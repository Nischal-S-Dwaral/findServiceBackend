package org.msc.web.dev.service.impl.notification;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.Notification;
import org.msc.web.dev.model.notification.get.GetUnseenNotificationsCountResponse;
import org.msc.web.dev.repository.NotificationRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
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
public class GetUnseenNotificationsCountImpl implements IUseCaseImplementation<
        String, List<Notification>, GetUnseenNotificationsCountResponse> {

    @Autowired
    private NotificationRepository repository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.registerAdaptor(
                ServiceEnum.NOTIFICATION, UseCasesEnums.GET_UNSEEN_NOTIFICATIONS_COUNT, this
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
                    .filter(n -> !n.isSeen()) // only include notifications where seen is false
                    .collect(Collectors.toList());

        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get Notification List from FireBase "+exception.getMessage());
        }
    }

    @Override
    public GetUnseenNotificationsCountResponse postProcess(List<Notification> notifications) {
        return new GetUnseenNotificationsCountResponse(notifications.size());
    }
}

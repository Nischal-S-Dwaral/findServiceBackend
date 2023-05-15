package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.constants.NotificationConstants;
import org.msc.web.dev.constants.ServiceRequestConstants;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.Notification;
import org.msc.web.dev.model.collections.ServiceRequest;
import org.msc.web.dev.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class NotificationRepository {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    public void createForUpdateInService(String serviceId) {

        try {
            List<QueryDocumentSnapshot> serviceResultListQuery =
                    serviceRequestRepository.getServiceRequestList(ServiceRequestConstants.HEADER_SERVICE_ID, serviceId)
                            .get().getDocuments();

            List<ServiceRequest> serviceRequestList = serviceResultListQuery.stream()
                    .map(d -> d.toObject(ServiceRequest.class))
                    .collect(Collectors.toList());

            for (ServiceRequest serviceRequest : serviceRequestList) {
                Notification notification = new Notification();
                notification.setType(NotificationConstants.TYPE_GENERAL);
                notification.setSeen(false);
                notification.setMessage("Update in Service - " + serviceRequest.getServiceName());
                notification.setCustomerId(serviceRequest.getCustomerId());
                notification.setRedirectUrl("/service/"+serviceId);
                notification.setTimestamp(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

                Firestore firestore = FirestoreClient.getFirestore();
                CollectionReference databaseReference  = firestore.collection(Constants.NOTIFICATION_COLLECTION);
                DocumentReference documentReference = databaseReference.document();
                notification.setId(documentReference.getId());
                documentReference.create(notification);
            }
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get service request list from FireBase- "+exception.getMessage());
        }
    }

    public void createForUpdateByServiceProvider(String serviceRequestId, String text) {

        try {
            ApiFuture<DocumentSnapshot> serviceRequestDocument = serviceRequestRepository.findById(serviceRequestId);
            Map<String, Object> documentMap = serviceRequestDocument.get().getData();
            ServiceRequest serviceRequest = JsonUtil.toObject(documentMap, ServiceRequest.class);

            Notification notification = new Notification();
            notification.setType(NotificationConstants.TYPE_UPDATE_SERVICE_REQUEST);
            notification.setSeen(false);
            notification.setMessage(text);
            notification.setCustomerId(serviceRequest.getCustomerId());
            notification.setRedirectUrl("/request/"+serviceRequestId);
            notification.setTimestamp(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            Firestore firestore = FirestoreClient.getFirestore();
            CollectionReference databaseReference  = firestore.collection(Constants.NOTIFICATION_COLLECTION);
            DocumentReference documentReference = databaseReference.document();
            notification.setId(documentReference.getId());
            documentReference.create(notification);
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get service request list from FireBase- "+exception.getMessage());
        }
    }

    public void createForReview(String serviceRequestId) {

        try {
            ApiFuture<DocumentSnapshot> serviceRequestDocument = serviceRequestRepository.findById(serviceRequestId);
            Map<String, Object> documentMap = serviceRequestDocument.get().getData();
            ServiceRequest serviceRequest = JsonUtil.toObject(documentMap, ServiceRequest.class);

            Notification notification = new Notification();
            notification.setType(NotificationConstants.TYPE_REVIEW);
            notification.setSeen(false);
            notification.setMessage("Review Requested for : "+ serviceRequestId);
            notification.setCustomerId(serviceRequest.getCustomerId());
            notification.setTimestamp(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            Firestore firestore = FirestoreClient.getFirestore();
            CollectionReference databaseReference  = firestore.collection(Constants.NOTIFICATION_COLLECTION);
            DocumentReference documentReference = databaseReference.document();
            notification.setId(documentReference.getId());
            notification.setRedirectUrl("/review/"+serviceRequest.getServiceId()+"?nid="+documentReference.getId());
            documentReference.create(notification);
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get service request list from FireBase- "+exception.getMessage());
        }
    }

    public ApiFuture<QuerySnapshot> geAllNotifications(String customerId) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.NOTIFICATION_COLLECTION);
        return databaseReference.whereEqualTo("customerId", customerId)
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING).get();
    }

    public ApiFuture<WriteResult> updateSeenStatus(String notificationId, Map<String, Object> fieldMap) {

        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.NOTIFICATION_COLLECTION);

        try {
            ApiFuture<QuerySnapshot> apiFuture = databaseReference.whereEqualTo("id", notificationId).get();
            String documentId = apiFuture.get().getDocuments().get(0).getId();
            return databaseReference.document(documentId).update(fieldMap);
        } catch (Exception exception) {
            throw new InternalServerError(exception.getMessage());
        }
    }
}

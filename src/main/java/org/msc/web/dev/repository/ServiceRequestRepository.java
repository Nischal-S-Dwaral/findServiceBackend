package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.model.collections.ServiceRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceRequestRepository {
    public ApiFuture<WriteResult> create(ServiceRequest serviceRequest) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_REQUEST_COLLECTION);
        DocumentReference documentReference = databaseReference.document();
        serviceRequest.setId(documentReference.getId());
        return documentReference.create(serviceRequest);
    }

    public ApiFuture<DocumentSnapshot> findById(String requestId) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_REQUEST_COLLECTION);
        return databaseReference.document(requestId).get();
    }

    public ApiFuture<QuerySnapshot> getServiceRequestList(String field, String requestId) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_REQUEST_COLLECTION);
        return databaseReference.whereEqualTo(field, requestId).get();
    }

    public ApiFuture<WriteResult> updateStatus(String serviceRequestId, Map<String, Object> fieldMap) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_REQUEST_COLLECTION);
        return databaseReference.document(serviceRequestId).update(fieldMap);
    }
}

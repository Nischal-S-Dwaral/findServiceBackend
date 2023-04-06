package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.model.collections.ServiceRequest;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestRepository {
    public ApiFuture<WriteResult> create(ServiceRequest serviceRequest) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_REQUEST_COLLECTION);
        DocumentReference documentReference = databaseReference.document();
        serviceRequest.setId(documentReference.getId());
        return documentReference.create(serviceRequest);
    }
}

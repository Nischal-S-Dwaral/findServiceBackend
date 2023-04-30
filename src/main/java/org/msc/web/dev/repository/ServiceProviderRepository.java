package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.model.collections.ServiceProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceProviderRepository {

    public ApiFuture<WriteResult> create(ServiceProvider serviceProvider) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_PROVIDER_COLLECTION);
        DocumentReference documentReference = databaseReference.document();
        return documentReference.create(serviceProvider);
    }

    public ApiFuture<DocumentSnapshot> findById(String id) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_PROVIDER_COLLECTION);
        return databaseReference.document(id).get();
    }

    public ApiFuture<WriteResult> updateStatus(String id, Map<String, Object> fieldMap) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_PROVIDER_COLLECTION);
        return databaseReference.document(id).update(fieldMap);
    }

    public ApiFuture<WriteResult> delete(String id) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_PROVIDER_COLLECTION);
        return databaseReference.document(id).delete();
    }

    public ApiFuture<QuerySnapshot> getServiceProviderListByStatus(String status) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference = firestore.collection(Constants.SERVICE_PROVIDER_COLLECTION);
        return databaseReference
                .whereGreaterThanOrEqualTo("approvalStatus", status)
                .whereLessThanOrEqualTo("approvalStatus", status).get();
    }

    public ApiFuture<QuerySnapshot> findBySPid(String id) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference = firestore.collection(Constants.SERVICE_PROVIDER_COLLECTION);
        return databaseReference.whereEqualTo("id", id).get();


    }
}

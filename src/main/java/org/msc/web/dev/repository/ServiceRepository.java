package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.model.collections.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceRepository {

    public ApiFuture<WriteResult> create(Service service) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_COLLECTION);
        DocumentReference documentReference = databaseReference.document();
        service.setId(documentReference.getId());
        return documentReference.create(service);
    }
}

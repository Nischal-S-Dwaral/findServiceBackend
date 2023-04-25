package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.model.collections.CommentsServiceProviderRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CommentsServiceProviderRepository {

    public ApiFuture<WriteResult> add(CommentsServiceProviderRequest commentsServiceProviderRequest) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.COMMENTS_SERVICE_PROVIDER_COLLECTION);
        DocumentReference documentReference = databaseReference.document();
        commentsServiceProviderRequest.setId(documentReference.getId());
        commentsServiceProviderRequest.setTimestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        return documentReference.create(commentsServiceProviderRequest);
    }

    public ApiFuture<QuerySnapshot> getCommentList(String serviceProviderId) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.COMMENTS_SERVICE_PROVIDER_COLLECTION);
        return databaseReference.whereEqualTo("serviceProviderId", serviceProviderId).get();
    }
}

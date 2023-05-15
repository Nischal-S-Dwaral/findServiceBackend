package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.model.collections.CommentsServiceRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CommentsServiceRequestRepository {

    public ApiFuture<WriteResult> add(CommentsServiceRequest commentsServiceRequest) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.COMMENTS_SERVICE_REQUEST_COLLECTION);
        DocumentReference documentReference = databaseReference.document();
        commentsServiceRequest.setId(documentReference.getId());
        commentsServiceRequest.setTimestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        return documentReference.create(commentsServiceRequest);
    }

    public ApiFuture<QuerySnapshot> getCommentList(String serviceRequestId) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.COMMENTS_SERVICE_REQUEST_COLLECTION);
        return databaseReference.whereEqualTo("serviceRequestId", serviceRequestId)
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING).get();
    }
}

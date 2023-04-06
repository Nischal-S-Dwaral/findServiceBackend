package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.model.collections.Review;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ReviewRepository {

    public ApiFuture<WriteResult> add(Review review) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.REVIEW_COLLECTION);
        DocumentReference documentReference = databaseReference.document();
        review.setId(documentReference.getId());
        review.setTimeStamp(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return documentReference.create(review);
    }

    public ApiFuture<QuerySnapshot> getReviewList(String serviceId) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.REVIEW_COLLECTION);
        return databaseReference.whereEqualTo("serviceId", serviceId).get();
    }

    public ApiFuture<DocumentSnapshot> findById(String id) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.REVIEW_COLLECTION);
        return databaseReference.document(id).get();
    }

    public ApiFuture<WriteResult> delete(String id) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.REVIEW_COLLECTION);
        return databaseReference.document(id).delete();
    }
}

package org.msc.web.dev.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.msc.web.dev.constants.Constants;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.Service;
import org.msc.web.dev.utils.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class ServiceRepository {

    public ApiFuture<WriteResult> create(Service service) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_COLLECTION);
        DocumentReference documentReference = databaseReference.document();
        service.setId(documentReference.getId());
        return documentReference.create(service);
    }

    public ApiFuture<WriteResult> updateTotalReviewOnAdd(String serviceId, String rating) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_COLLECTION);
        try {
            ApiFuture<DocumentSnapshot>  documentSnapshotApiFuture = databaseReference.document(serviceId).get();
            Map<String, Object> documentMap = documentSnapshotApiFuture.get().getData();

            Service service = JsonUtil.toObject(documentMap, Service.class);

            String totalRating = Float.parseFloat(service.getTotalRating()) != 0f
                    ? String.valueOf(Float.parseFloat(service.getTotalRating()) + Float.parseFloat(rating))
                    : rating;

            String numberOfRatings = Integer.parseInt(service.getNumberOfRatings()) != 0
                    ? String.valueOf(Integer.parseInt(service.getNumberOfRatings()) + 1)
                    : String.valueOf(1);

            service.setTotalRating(totalRating);
            service.setNumberOfRatings(numberOfRatings);

            return databaseReference.document(serviceId).update(JsonUtil.toMap(service));

        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Error while updating the review total: "+exception.getMessage());
        }
    }

    public ApiFuture<WriteResult> updateTotalReviewOnDelete(String serviceId, String rating) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference databaseReference  = firestore.collection(Constants.SERVICE_COLLECTION);
        try {
            ApiFuture<DocumentSnapshot>  documentSnapshotApiFuture = databaseReference.document(serviceId).get();
            Map<String, Object> documentMap = documentSnapshotApiFuture.get().getData();

            Service service = JsonUtil.toObject(documentMap, Service.class);

            String numberOfRatings = Integer.parseInt(service.getNumberOfRatings()) != 0
                    ? String.valueOf(Integer.parseInt(service.getNumberOfRatings()) - 1)
                    : String.valueOf(0);

            String totalRating = Integer.parseInt(numberOfRatings) != 0
                    ? String.valueOf(Float.parseFloat(service.getTotalRating()) - Float.parseFloat(rating))
                    : String.valueOf(0f);

            service.setTotalRating(totalRating);
            service.setNumberOfRatings(numberOfRatings);

            return databaseReference.document(serviceId).update(JsonUtil.toMap(service));

        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Error while updating the review total: "+exception.getMessage());
        }
    }
}

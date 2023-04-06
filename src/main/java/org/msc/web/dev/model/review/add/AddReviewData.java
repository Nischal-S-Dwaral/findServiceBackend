package org.msc.web.dev.model.review.add;

import com.google.cloud.firestore.WriteResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddReviewData {

    private WriteResult reviewData;
    private WriteResult updateServiceData;
}

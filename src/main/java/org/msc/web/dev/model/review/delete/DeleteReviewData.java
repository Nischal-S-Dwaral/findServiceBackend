package org.msc.web.dev.model.review.delete;

import com.google.cloud.firestore.WriteResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteReviewData {

    private WriteResult reviewDeleteData;
    private WriteResult updateServiceData;
}

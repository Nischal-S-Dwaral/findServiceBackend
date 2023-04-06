package org.msc.web.dev.model.review.add;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class AddReviewResponse extends RestApiResponse {

    private Timestamp reviewAddTimestamp;
    private Timestamp serviceUpdateTimestamp;
}

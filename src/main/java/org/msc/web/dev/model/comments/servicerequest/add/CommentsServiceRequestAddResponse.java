package org.msc.web.dev.model.comments.servicerequest.add;

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
public class CommentsServiceRequestAddResponse extends RestApiResponse {

    private Timestamp timestamp;
}

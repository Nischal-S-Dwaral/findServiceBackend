package org.msc.web.dev.model.comments.servicerequest.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.model.collections.CommentsServiceRequest;

import java.util.List;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CommentsServiceRequestGetResponse extends RestApiResponse {

    private List<CommentsServiceRequest> commentList;
}

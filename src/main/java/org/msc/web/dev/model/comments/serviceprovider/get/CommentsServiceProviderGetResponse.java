package org.msc.web.dev.model.comments.serviceprovider.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.model.collections.CommentsServiceProviderRequest;

import java.util.List;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CommentsServiceProviderGetResponse extends RestApiResponse {

    private List<CommentsServiceProviderRequest> commentList;
}

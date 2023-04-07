package org.msc.web.dev.model.collections;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.msc.web.dev.model.comments.servicerequest.add.CommentsServiceRequestAddRequest;

@Data
@NoArgsConstructor
public class CommentsServiceRequest {

    private String id;
    private String serviceRequestId;
    private String name;
    private String text;
    private String timestamp;

    public CommentsServiceRequest(CommentsServiceRequestAddRequest commentsServiceRequestAddRequest) {
        this.name = commentsServiceRequestAddRequest.getName();
        this.serviceRequestId = commentsServiceRequestAddRequest.getServiceRequestId();
        this.text = commentsServiceRequestAddRequest.getText();
    }
}

package org.msc.web.dev.model.comments.servicerequest.add;

import lombok.Data;

@Data
public class CommentsServiceRequestAddRequest {

    private String serviceRequestId;
    private String name;
    private String text;
    private boolean fromServiceProvider;
}

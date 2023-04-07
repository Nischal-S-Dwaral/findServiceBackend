package org.msc.web.dev.model.collections;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.msc.web.dev.model.comments.serviceprovider.add.CommentsServiceProviderAddRequest;

@Data
@NoArgsConstructor
public class CommentsServiceProviderRequest {

    private String id;
    private String serviceProviderId;
    private String name;
    private String text;
    private String timestamp;

    public CommentsServiceProviderRequest(CommentsServiceProviderAddRequest addRequest) {
        this.name = addRequest.getName();
        this.text = addRequest.getText();
        this.serviceProviderId = addRequest.getServiceProviderId();
    }
}

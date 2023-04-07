package org.msc.web.dev.model.comments.serviceprovider.add;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentsServiceProviderAddRequest {

    private String serviceProviderId;
    private String name;
    private String text;
}

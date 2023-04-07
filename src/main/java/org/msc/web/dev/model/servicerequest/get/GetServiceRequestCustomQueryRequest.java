package org.msc.web.dev.model.servicerequest.get;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetServiceRequestCustomQueryRequest {

    private String requestId;
    private String field;
}

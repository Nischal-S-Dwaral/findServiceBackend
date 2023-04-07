package org.msc.web.dev.model.servicerequest.update;

import lombok.Data;

@Data
public class ServiceRequestStatusUpdateRequest {

    private String serviceRequestId;
    private String updatedStatus;
}

package org.msc.web.dev.model.serviceprovider.update;

import lombok.Data;

@Data
public class UpdateStatusServiceProviderRequest {

    private String serviceProviderId;
    private String updateStatus;
}

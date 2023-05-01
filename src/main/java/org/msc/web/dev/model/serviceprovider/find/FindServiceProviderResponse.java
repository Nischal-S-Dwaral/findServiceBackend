package org.msc.web.dev.model.serviceprovider.find;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.model.collections.ServiceProvider;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class FindServiceProviderResponse extends RestApiResponse {

    private String email;
    private String id;
    private String address;
    private String name;
    private String description;
    private String approvalStatus;
    private String position;

    public FindServiceProviderResponse(ServiceProvider serviceProvider) {
        this.email = serviceProvider.getEmail();
        this.id = serviceProvider.getId();
        this.name = serviceProvider.getName();
        this.address = serviceProvider.getAddress();
        this.description = serviceProvider.getDescription();
        this.approvalStatus = serviceProvider.getApprovalStatus();
        this.position = serviceProvider.getPosition();
    }
}

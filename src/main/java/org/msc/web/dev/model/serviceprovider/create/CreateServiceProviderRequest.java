package org.msc.web.dev.model.serviceprovider.create;

import lombok.Data;

@Data
public class CreateServiceProviderRequest {

    private String email;
    private String name;
    private String id;
    private String address;
    private String description;
}

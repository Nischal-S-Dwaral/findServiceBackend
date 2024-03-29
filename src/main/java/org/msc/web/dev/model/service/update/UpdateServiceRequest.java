package org.msc.web.dev.model.service.update;

import lombok.Data;

@Data
public class UpdateServiceRequest {

    private String serviceId;
    private String location;
    private String description;
    private String price;
    private String availability;
}

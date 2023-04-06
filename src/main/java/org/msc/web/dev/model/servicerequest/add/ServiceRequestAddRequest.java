package org.msc.web.dev.model.servicerequest.add;

import lombok.Data;

@Data
public class ServiceRequestAddRequest {

    private String serviceId;
    private String customerId;
    private String customerName;
    private String description;
    private String date;
    private String time;
    private String address;
}

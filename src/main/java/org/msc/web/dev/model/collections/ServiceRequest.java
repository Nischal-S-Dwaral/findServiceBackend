package org.msc.web.dev.model.collections;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.msc.web.dev.model.servicerequest.add.ServiceRequestAddRequest;

@Data
@NoArgsConstructor
public class ServiceRequest {

    private String id;
    private String serviceId;
    private String serviceProviderId;
    private String serviceName;
    private String serviceCategory;
    private String customerId;
    private String customerName;
    private String description;
    private String date;
    private String time;
    private String address;
    private String status;
    private String price;
    private String createdAt;
    private String updatedAt;

    public ServiceRequest(ServiceRequestAddRequest serviceRequestAddRequest) {
        this.serviceId = serviceRequestAddRequest.getServiceId();
        this.customerId = serviceRequestAddRequest.getCustomerId();
        this.customerName = serviceRequestAddRequest.getCustomerName();
        this.description = serviceRequestAddRequest.getDescription();
        this.date = serviceRequestAddRequest.getDate();
        this.time = serviceRequestAddRequest.getTime();
        this.address = serviceRequestAddRequest.getAddress();
    }
}

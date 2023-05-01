package org.msc.web.dev.model.collections;

import lombok.Data;

/**
 * @author Nischal SD
 * POJO for the firebase collection "serviceProvider"
 */
@Data
public class ServiceProvider {

    private String id;
    private String email;
    private String address;
    private String name;
    private String description;
    private String approvalStatus;
    private String position;
}

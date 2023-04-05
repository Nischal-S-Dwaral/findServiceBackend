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
    private String password;
    private String address;
    private String description;
    private String approvalStatus;
}

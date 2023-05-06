package org.msc.web.dev.model.collections;

import lombok.Data;

/**
 * @author Nischal SD
 * POJO for the firebase collection "notifications"
 */
@Data
public class Notification {

    private String id;
    private String type;
    private String message;
    private String customerId;
    private boolean seen;
    private String redirectUrl;
    private String timestamp;
}

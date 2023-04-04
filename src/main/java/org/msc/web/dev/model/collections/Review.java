package org.msc.web.dev.model.collections;

import lombok.Data;

@Data
public class Review {

    private String id;
    private String serviceId;
    private String customerName;
    private String rating;
    private String comment;
    private String timeStamp;
}

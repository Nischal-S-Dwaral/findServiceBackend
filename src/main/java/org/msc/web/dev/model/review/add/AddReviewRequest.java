package org.msc.web.dev.model.review.add;

import lombok.Data;

@Data
public class AddReviewRequest {

    private String serviceId;
    private String customerId;
    private String rating;
    private String comment;
}

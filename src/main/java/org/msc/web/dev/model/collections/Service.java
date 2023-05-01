package org.msc.web.dev.model.collections;

import lombok.Data;
import org.msc.web.dev.model.service.Position;

import java.util.List;

@Data
public class Service {

    private String id;
    private String name;
    private String serviceProviderId;
    private String location;
    private String description;
    private String price;
    private String category;
    private String availability;
    private String createdAt;
    private String updatedAt;
    private List<String> photos;
    private String totalRating;
    private String numberOfRatings;
    private Position position;
}

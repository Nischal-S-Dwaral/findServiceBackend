package org.msc.web.dev.model.service.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.model.collections.Service;

import java.util.List;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetServiceByIdResponse extends RestApiResponse {

    private String id;
    private String serviceProviderId;
    private String location;
    private String description;
    private String price;
    private String category;
    private String availability;
    private List<String> photos;
    private String totalRating;
    private String numberOfRatings;

    public GetServiceByIdResponse(Service service) {
        this.id = service.getId();
        this.serviceProviderId = service.getServiceProviderId();
        this.location = service.getLocation();
        this.description = service.getDescription();
        this.price = service.getPrice();
        this.category = service.getCategory();
        this.availability = service.getAvailability();
        this.photos = service.getPhotos();
        this.totalRating = service.getTotalRating();
        this.numberOfRatings = service.getNumberOfRatings();
    }
}

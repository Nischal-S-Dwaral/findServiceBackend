package org.msc.web.dev.model.notification.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.model.collections.Notification;

import java.util.List;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetAllNotificationsResponse extends RestApiResponse {

    private List<Notification> updatesServiceRequest;
    private List<Notification> reviewRequest;
    private List<Notification> general;
}

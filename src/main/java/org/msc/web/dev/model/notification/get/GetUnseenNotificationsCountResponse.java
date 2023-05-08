package org.msc.web.dev.model.notification.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetUnseenNotificationsCountResponse extends RestApiResponse {

    private int notificationCount;
}

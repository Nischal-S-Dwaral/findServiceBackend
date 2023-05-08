package org.msc.web.dev.model.notification.update;

import lombok.Data;

@Data
public class UpdateNotificationSeenStatusRequest {

    private String notificationId;
    private boolean seen;
}

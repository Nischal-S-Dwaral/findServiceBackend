package org.msc.web.dev.model.servicerequest.delete;

import com.google.cloud.Timestamp;
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
public class DeleteServiceRequestResponse extends RestApiResponse {
    private Timestamp timestamp;
}

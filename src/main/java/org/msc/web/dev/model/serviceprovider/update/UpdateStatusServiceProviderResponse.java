package org.msc.web.dev.model.serviceprovider.update;

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
public class UpdateStatusServiceProviderResponse extends RestApiResponse {
    private Timestamp timestamp;
}


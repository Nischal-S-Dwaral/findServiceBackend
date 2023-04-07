package org.msc.web.dev.model.servicerequest.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.model.collections.ServiceRequest;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetServiceRequestByIdResponse extends RestApiResponse {

    private ServiceRequest serviceRequest;
}

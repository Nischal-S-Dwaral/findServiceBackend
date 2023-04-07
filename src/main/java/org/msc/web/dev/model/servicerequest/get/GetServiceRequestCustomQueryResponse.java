package org.msc.web.dev.model.servicerequest.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.model.collections.ServiceRequest;

import java.util.List;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetServiceRequestCustomQueryResponse extends RestApiResponse {

    private List<ServiceRequest> serviceRequestList;
}

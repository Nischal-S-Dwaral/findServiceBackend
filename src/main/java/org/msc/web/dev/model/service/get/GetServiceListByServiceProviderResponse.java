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
public class GetServiceListByServiceProviderResponse extends RestApiResponse {

    private List<Service> serviceList;
    private String serviceProviderName;
}

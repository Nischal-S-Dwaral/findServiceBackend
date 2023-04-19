package org.msc.web.dev.model.serviceprovider.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.msc.web.dev.model.RestApiResponse;
import org.msc.web.dev.model.collections.ServiceProvider;

import java.util.List;

/**
 * @author nischal.sd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetServiceProviderListByStatusResponse extends RestApiResponse {

    private List<ServiceProvider> serviceProviderList;
}
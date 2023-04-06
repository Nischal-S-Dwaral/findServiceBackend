package org.msc.web.dev.model.service.delete;

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
public class DeleteServiceResponse extends RestApiResponse {

    private int numberOfDeletedServices;
}

package org.msc.web.dev.model.service.get;

import com.google.cloud.firestore.DocumentSnapshot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msc.web.dev.model.collections.Service;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetServiceListByServiceProviderData {

    private List<Service> serviceList;
    private DocumentSnapshot documentSnapshot;
}
package org.msc.web.dev.model.service.add;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
public class AddServiceRequest {

    private List<MultipartFile> photos;
    private Map<String, String[]> parameterMap;
}

package org.msc.web.dev.service;

import org.msc.web.dev.model.RestApiResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface IUseCaseImplementation<R, S, T extends RestApiResponse> {

  R preProcess(HttpServletRequest request) throws IOException;
  S process(R r);
  T postProcess(S s);

  default T execute(HttpServletRequest request) throws IOException {
    return postProcess(process(preProcess(request)));
  }
}

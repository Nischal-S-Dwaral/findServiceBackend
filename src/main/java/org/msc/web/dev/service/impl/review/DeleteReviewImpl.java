package org.msc.web.dev.service.impl.review;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.constants.ReviewConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.review.delete.DeleteReviewResponse;
import org.msc.web.dev.repository.ReviewRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class DeleteReviewImpl implements IUseCaseImplementation<
        String, WriteResult, DeleteReviewResponse> {

    @Autowired
    private ReviewRepository reviewRepository;

    @PostConstruct
    public void initProp() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.REVIEW, UseCasesEnums.DELETE, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ReviewConstants.REVIEW_ID_HEADER))) {
            throw new BadRequest("ID is required get find");
        }
        return queryParams.get(ReviewConstants.REVIEW_ID_HEADER);
    }

    @Override
    public WriteResult process(String reviewId) {
        try {
            ApiFuture<WriteResult> writeResultApiFuture = reviewRepository.delete(reviewId);
            return writeResultApiFuture.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to delete Review from FireBase "+exception.getMessage());
        }
    }

    @Override
    public DeleteReviewResponse postProcess(WriteResult writeResult) {
        return new DeleteReviewResponse(writeResult.getUpdateTime());
    }
}

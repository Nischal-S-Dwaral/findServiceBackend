package org.msc.web.dev.service.impl.review;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.msc.web.dev.constants.ReviewConstants;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.Review;
import org.msc.web.dev.model.review.get.GetReviewListResponse;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class GetReviewListImpl implements IUseCaseImplementation<
        String, List<Review>, GetReviewListResponse> {

    @Autowired
    private ReviewRepository reviewRepository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.REVIEW, UseCasesEnums.GET_REVIEW_LIST, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtil.getQueryParams(request);
        if (!CommonUtils.checkIfObjectIsNotNull(queryParams.get(ReviewConstants.SERVICE_ID_HEADER))) {
            throw new BadRequest("ID is required get find");
        }
        return queryParams.get(ReviewConstants.SERVICE_ID_HEADER);
    }

    @Override
    public List<Review> process(String serviceId) {
        try {
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = reviewRepository.getReviewList(serviceId);
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();
            return queryDocumentSnapshotList.stream()
                    .map(d -> d.toObject(Review.class))
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get review list from FireBase for service id- "+serviceId+ " :"+exception.getMessage());
        }
    }

    @Override
    public GetReviewListResponse postProcess(List<Review> reviews) {
        return new GetReviewListResponse(reviews);
    }
}

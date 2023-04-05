package org.msc.web.dev.service.impl.review;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.Review;
import org.msc.web.dev.model.review.add.AddReviewRequest;
import org.msc.web.dev.model.review.add.AddReviewResponse;
import org.msc.web.dev.repository.ReviewRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@Service
public class AddReviewImpl implements IUseCaseImplementation<AddReviewRequest, WriteResult, AddReviewResponse> {

    @Autowired
    private ReviewRepository reviewRepository;

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.REVIEW, UseCasesEnums.ADD, this);
    }

    @Override
    public AddReviewRequest preProcess(HttpServletRequest request) {
        AddReviewRequest addRequest = RequestUtil.getRequestData(
                request,
                AddReviewRequest.class);
        if (CommonUtils.isAnyFieldEmpty(addRequest.getClass().getDeclaredFields(), addRequest)) {
            throw new BadRequest("All fields are required to serve the data.");
        }
        return addRequest;
    }

    @Override
    public WriteResult process(AddReviewRequest addReviewRequest) {

        Review review = new Review();
        review.setServiceId(addReviewRequest.getServiceId());
        //TODO: To get the customer name from Customer Collection
        review.setCustomerName("Name - "+addReviewRequest.getCustomerId());
        review.setRating(addReviewRequest.getRating());
        review.setComment(addReviewRequest.getComment());

        try {
            ApiFuture<WriteResult> data = reviewRepository.add(review);
            return data.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to save Review in FireBase "+exception.getMessage());
        }
    }

    @Override
    public AddReviewResponse postProcess(WriteResult documentReference) {
        return new AddReviewResponse(documentReference.getUpdateTime());
    }
}

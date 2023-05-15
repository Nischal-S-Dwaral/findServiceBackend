package org.msc.web.dev.service.impl.review;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.msc.web.dev.enums.ServiceEnum;
import org.msc.web.dev.enums.UseCasesEnums;
import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.InternalServerError;
import org.msc.web.dev.model.collections.Review;
import org.msc.web.dev.model.review.add.AddReviewData;
import org.msc.web.dev.model.review.add.AddReviewRequest;
import org.msc.web.dev.model.review.add.AddReviewResponse;
import org.msc.web.dev.repository.NotificationRepository;
import org.msc.web.dev.repository.ReviewRepository;
import org.msc.web.dev.repository.ServiceRepository;
import org.msc.web.dev.service.IUseCaseImplementation;
import org.msc.web.dev.service.UseCasesAdaptorFactory;
import org.msc.web.dev.utils.CommonUtils;
import org.msc.web.dev.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class AddReviewImpl implements IUseCaseImplementation<AddReviewRequest, AddReviewData, AddReviewResponse> {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

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
    public AddReviewData process(AddReviewRequest addReviewRequest) {

        Review review = new Review();
        review.setServiceId(addReviewRequest.getServiceId());
        review.setCustomerName(addReviewRequest.getCustomerName());
        review.setRating(addReviewRequest.getRating());
        review.setComment(addReviewRequest.getComment());

        try {
            ApiFuture<WriteResult> reviewData = reviewRepository.add(review);
            ApiFuture<WriteResult> updateServiceData =
                    serviceRepository.updateTotalReviewOnAdd(review.getServiceId(), review.getRating());

            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("seen", true);
            notificationRepository.updateSeenStatus(
                    addReviewRequest.getNotificationId(), fieldMap
            );
            
            return new AddReviewData(reviewData.get(), updateServiceData.get());
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to save Review in FireBase "+exception.getMessage());
        }
    }

    @Override
    public AddReviewResponse postProcess(AddReviewData addReviewData) {
        return new AddReviewResponse(
                addReviewData.getReviewData().getUpdateTime(),
                addReviewData.getUpdateServiceData().getUpdateTime());
    }
}

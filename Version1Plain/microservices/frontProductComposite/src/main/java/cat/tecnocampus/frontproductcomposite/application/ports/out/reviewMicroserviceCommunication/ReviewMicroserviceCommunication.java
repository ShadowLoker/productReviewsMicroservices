package cat.tecnocampus.frontproductcomposite.application.ports.out.reviewMicroserviceCommunication;

import cat.tecnocampus.frontproductcomposite.application.services.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewMicroserviceCommunication {

    List<Review> getReviewsFromProduct(long productId);
    Review createReview(Review product);
}

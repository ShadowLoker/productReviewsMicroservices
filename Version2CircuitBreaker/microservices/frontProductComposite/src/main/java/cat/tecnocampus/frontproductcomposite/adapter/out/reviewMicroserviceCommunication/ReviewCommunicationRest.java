package cat.tecnocampus.frontproductcomposite.adapter.out.reviewMicroserviceCommunication;

import cat.tecnocampus.frontproductcomposite.application.ports.out.reviewMicroserviceCommunication.ReviewMicroserviceCommunication;
import cat.tecnocampus.frontproductcomposite.application.services.Review;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ReviewCommunicationRest implements ReviewMicroserviceCommunication {
    private final RestClient restClient;

    public ReviewCommunicationRest(@Qualifier("reviewRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Review> getReviewsFromProduct(long productId, int delay, int faultPercent) {
        return restClient.get()
                .uri("/product/" + productId + "?delay=" + delay + "&faultPercent=" + faultPercent)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Review>>() {
                });
    }

    @Override
    public Review createReview(Review review) {
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON) // Set the content type to JSON
                .body(review) // Set the request body
                .retrieve()
                .body(Review.class); // Deserialize the response to ProductCompositereturn null;
    }
}

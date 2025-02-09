package cat.tecnocampus.frontproductcomposite.adapter.out.reviewMicroserviceCommunication;

import cat.tecnocampus.frontproductcomposite.application.ports.out.productMicroserviceCommunication.ProductMicroserviceCommunication;
import cat.tecnocampus.frontproductcomposite.application.ports.out.reviewMicroserviceCommunication.ReviewMicroserviceCommunication;
import cat.tecnocampus.frontproductcomposite.application.services.ProductComposite;
import cat.tecnocampus.frontproductcomposite.application.services.Review;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class ReviewCommunicationRest implements ReviewMicroserviceCommunication {
    private final RestClient restClient;

    public ReviewCommunicationRest(@Qualifier("reviewRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Review> getReviewsFromProduct(long productId) {
        return restClient.get()
                .uri("/product/" + productId)
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

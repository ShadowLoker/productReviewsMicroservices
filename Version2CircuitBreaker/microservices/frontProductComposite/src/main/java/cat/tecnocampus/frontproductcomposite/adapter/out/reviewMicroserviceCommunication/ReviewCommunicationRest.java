package cat.tecnocampus.frontproductcomposite.adapter.out.reviewMicroserviceCommunication;

import cat.tecnocampus.frontproductcomposite.application.ports.out.reviewMicroserviceCommunication.ReviewMicroserviceCommunication;
import cat.tecnocampus.frontproductcomposite.application.services.Review;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ReviewCommunicationRest implements ReviewMicroserviceCommunication {
    private final RestClient restClient;
    private final TimeLimiterCall timeLimiterCall;

    public ReviewCommunicationRest(@Qualifier("reviewRestClient") RestClient restClient, TimeLimiterCall timeLimiterCall) {
        this.restClient = restClient;
        this.timeLimiterCall = timeLimiterCall;
    }

    @Override
    @CircuitBreaker(name = "review", fallbackMethod = "getReviewsFallbackValue")
    public List<Review> getReviewsFromProduct(long productId, int delay, int faultPercent) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        CompletableFuture<List<Review>> results = timeLimiterCall.getReviewsFromProductTimeLimiter(productId, delay, faultPercent);
        AtomicReference<List<Review>> finalResult = new AtomicReference<>(List.of(new Review(0, "time", "time", 5)));

        results.whenComplete((result, ex) -> {
            if (ex != null) {
                System.out.println("Exception " +
                        ex.getMessage() +
                        " on thread " +
                        Thread.currentThread().getName() +
                        " at " +
                        LocalDateTime.now().format(formatter));
            }
            if (result != null) {
                System.out.println(result + " on thread " + Thread.currentThread().getName());
                finalResult.set(result);
            }
        });
        return finalResult.get();
    }


/*
    private CompletionStage<List<Review>> getReviewsFromProductTimeLimiter (long productId, int delay, int faultPercent) {
        return CompletableFuture.supplyAsync(
                () -> restClient.get()
                        .uri("/product/" + productId + "?delay=" + delay + "&faultPercent=" + faultPercent)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<Review>>() {
                        })
        );

 */

    @Override
    public Review createReview(Review review) {
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON) // Set the content type to JSON
                .body(review) // Set the request body
                .retrieve()
                .body(Review.class); // Deserialize the response to ProductCompositereturn null;
    }

    private List<Review> getReviewsFallbackValue(long productId, int delay, int faultPercent, CallNotPermittedException e) {
        return List.of(new Review(0, "fallback", "fallback", 5));
    }
}

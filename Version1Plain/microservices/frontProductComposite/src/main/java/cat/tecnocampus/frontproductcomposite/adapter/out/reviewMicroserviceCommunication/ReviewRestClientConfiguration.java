package cat.tecnocampus.frontproductcomposite.adapter.out.reviewMicroserviceCommunication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ReviewRestClientConfiguration {
    private final String reviewServiceHost;
    private final String reviewServicePort;
    private final String reviewServiceUrl;

    public ReviewRestClientConfiguration(@Value("${app.review-service.host}") String reviewServiceHost,
                                         @Value("${app.review-service.port}")String reviewServicePort) {
        this.reviewServiceHost = reviewServiceHost;
        this.reviewServicePort = reviewServicePort;
        this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/reviews";
    }


    @Bean("reviewRestClient")
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(reviewServiceUrl)
                .build();
    }
}

package cat.tecnocampus.frontproductcomposite.adapter.out.productMicroserviceCommunication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductRestClientConfiguration {
    private final String productServiceHost;
    private final String productServicePort;
    private final String productServiceUrl;

    public ProductRestClientConfiguration(@Value("${app.product-service.host}") String productServiceHost,
                                          @Value("${app.product-service.port}")String productServicePort) {
        this.productServiceHost = productServiceHost;
        this.productServicePort = productServicePort;
        this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/products";
    }


    @Bean("productRestClient")
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }
}

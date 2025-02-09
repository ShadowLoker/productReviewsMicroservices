package cat.tecnocampus.frontproductcomposite;

import cat.tecnocampus.frontproductcomposite.application.ports.in.ProductCompositeCRUD;
import cat.tecnocampus.frontproductcomposite.application.ports.out.productMicroserviceCommunication.ProductMicroserviceCommunication;
import cat.tecnocampus.frontproductcomposite.application.services.ProductCompositeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBeanCreation {

    @Autowired
    private ProductMicroserviceCommunication productMicroserviceCommunication;

    @Bean
    public ProductCompositeCRUD productCompositeCRUD() {
        return new ProductCompositeService(productMicroserviceCommunication);
    }
}

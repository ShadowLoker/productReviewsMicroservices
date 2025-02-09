package cat.tecnocampus.frontproductcomposite.application.services;

import cat.tecnocampus.frontproductcomposite.application.ports.in.ProductCompositeCRUD;
import cat.tecnocampus.frontproductcomposite.application.ports.out.productMicroserviceCommunication.ProductMicroserviceCommunication;

import java.util.List;
import java.util.Optional;

public class ProductCompositeService implements ProductCompositeCRUD {
    private final ProductMicroserviceCommunication productMicroserviceCommunication;

    public ProductCompositeService(ProductMicroserviceCommunication productMicroserviceCommunication) {
        this.productMicroserviceCommunication = productMicroserviceCommunication;
    }

    @Override
    public List<ProductComposite> getProducts() {
        return productMicroserviceCommunication.getProducts();
    }

    @Override
    public Optional<ProductComposite> getProduct(long id) {
        return productMicroserviceCommunication.getProduct(id);
    }

    @Override
    public ProductComposite createProduct(ProductComposite product) {
        return productMicroserviceCommunication.createProduct(product);
    }
}

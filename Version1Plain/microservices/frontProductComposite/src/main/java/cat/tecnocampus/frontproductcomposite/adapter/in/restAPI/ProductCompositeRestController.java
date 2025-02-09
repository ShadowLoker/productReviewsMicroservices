package cat.tecnocampus.frontproductcomposite.adapter.in.restAPI;

import cat.tecnocampus.frontproductcomposite.application.ports.in.ProductCompositeCRUD;
import cat.tecnocampus.frontproductcomposite.application.services.ProductComposite;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeRestController {
    private final ProductCompositeCRUD productCompositeService;

    public ProductCompositeRestController(ProductCompositeCRUD productService) {
        this.productCompositeService = productService;
    }

    @GetMapping("/products")
    public List<ProductListWeb> getProducts() {
        return productCompositeService.getProducts().stream()
                .map(p -> new ProductListWeb(p.getId(),p.getName(),p.getDescription())).collect(Collectors.toList());
    }

    @GetMapping("/products/{id}")
    public ProductListWeb getProduct(@PathVariable long id) {
        return productCompositeService.getProduct(id)
                .map(p -> new ProductListWeb(p.getId(), p.getName(), p.getDescription()))
                .orElseThrow(() -> new ProductDoesNotExistException(id));
    }

    @PostMapping("/products")
    public void createProduct(@RequestBody ProductListWeb product) {
        productCompositeService.createProduct(new ProductComposite(product.getName(),product.getDescription()));
    }
}

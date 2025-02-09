package cat.tecnocampus.frontproductcomposite.adapter.in.restAPI;

public class ProductDoesNotExistException extends RuntimeException {
    public ProductDoesNotExistException(long id) {
        super("Product with id: " + id + " does not exist.");
    }
}

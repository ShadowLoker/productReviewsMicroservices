package cat.tecnocampus.frontproductcomposite.application.services;

public class ProductComposite {
    private long id;
    private String name;
    private String description;


    public ProductComposite() {
    }

    public ProductComposite(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

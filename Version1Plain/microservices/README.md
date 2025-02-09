# Microservice Architecture Example

This application demonstrates a microservice architecture composed of three distinct services:

1. **`product-service`**: Manages product-related information.
2. **`review-service`**: Handles product reviews.
3. **`front-product-service`**: Acts as a front-end service, aggregating data from the `product-service` and `review-service` to provide a 
unified view of products and their reviews. This is the only service exposed to external clients.

The `front-product-service` provides three straightforward REST endpoints:
- **GET `/products`**: Retrieves a list of products along with their associated reviews.
- **GET `/products/{id}`**: Fetches a single product and its reviews by ID.
- **POST `/products`**: Adds a new product and its reviews to the system.

When a request is made to the `front-product-service`, it orchestrates the following steps:
1. Fetches product details from the `product-service`.
2. Retrieves reviews from the `review-service`.
3. Combines the data into a single response for the client.

When a new product is added, the `front-product-service` sends requests to both the `product-service` and `review-service` to update their 
respective data stores.

---

## Microservices' Internal Architecture

Although the microservices are essentially CRUD (Create, Read, Update, Delete) applications, they adhere to the **Hexagonal Architecture** 
(also known as Ports and Adapters). This design pattern ensures separation of concerns and improves testability and maintainability. 
Each microservice is structured into the following packages:

- **`application`**:
    - **`ports`**: Defines the input (inbound) and output (outbound) interfaces.
    - **`services`**: Implements the core business logic, utilizing the domain model and interacting with the outbound ports.

- **`adapter`**:
    - **`in`**: Implements adapters for the REST API layer, which call the application logic through the input ports.
    - **`out`**: Implements adapters for external communication. These include:
        - **`persistence`**: Handles database interactions.
        - **`communication`**: Manages communication with other microservices.

---

## Database Design

While microservices are typically designed to have independent databases, this example simplifies the setup by using a single **MySQL database** 
hosted in a Docker container. Both the `product-service` and `review-service` share this database but operate on separate tables with no direct 
relationships between them. This approach reduces resource usage and complexity for demonstration purposes. In a production environment, 
each microservice would ideally have its own dedicated database to ensure loose coupling and scalability.

---

## Running the Application

The application can be run either locally or using Docker containers. In both cases, the MySQL database runs within a Docker container, so 
ensure the database container is started before launching the services.

### Running Locally
1. Launch each service as a Spring Boot application (e.g., using an IDE).
2. The services are configured to run on the following ports:
    - `front-product-service`: **8000**
    - `product-service`: **8081**
    - `review-service`: **8082**
3. Access the services at:
    - `http://localhost:8000` (front-end)
    - `http://localhost:8081` (product service)
    - `http://localhost:8082` (review service)
Note that all services are accessible through their respective ports.
   
### Running with Docker
1. Build the JAR files for each service by running: (you can do this from the IDE)
   ```bash
   mvn clean install
   ```
2. Start the application using Docker Compose: (you can do this from the IDE)
   ```bash
   docker-compose up
   ```
When running with Docker, the services form a private network where they can communicate with each other using their container hostnames. All services expose port **8080** internally, but only the `front-product-service` maps its port to the host machine (e.g., `http://localhost:8080`). The other services remain inaccessible from outside the Docker network.

See that, the `compose.yaml` file (located in the project root) defines the services and their configurations, while each service has 
its own `Dockerfile` in the root of their module.

---

## Version 1: Synchronous Communication (Request/Response)

In this version, the `front-product-service` communicates synchronously with the `product-service` and `review-service` using REST APIs. 
It leverages Spring's `RestClient`, a synchronous HTTP client with a fluent API, to make requests.

### Limitations
- **Lack of Resilience**: The system is not fault-tolerant. If either the `product-service` or `review-service` is unavailable, the 
`front-product-service` will fail to respond to client requests. For example, in an e-commerce scenario, the front-end should still display 
products even if the review service is down. However, the current implementation does not handle such failures gracefully.

## References and Further Reading
This example is inspired by the book **"Microservices with Spring Boot 3 and Spring Cloud, Third Edition. Magnus Larson. Ed. Packt"** and its accompanying GitHub repository:
- [GitHub Repository](https://github.com/PacktPublishing/Microservices-with-Spring-Boot-and-Spring-Cloud-Third-Edition/tree/SB3.2)


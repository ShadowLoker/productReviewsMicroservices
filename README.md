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

### Hexagonal Architecture
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

### DRY (Don't Repeat Yourself) Principle

Note that Microservices don't share code so many code is repeated between services. This is because each service is designed to be independent
and uncoupled of the others. In a microservice architecture, the DRY principle only applies within a service, not across services.

In our case, classes such as 'Review', 'ProductListWeb', 'ReviewListWeb' are repeated with the same exact code in the several services. In a real 
world scenario, when the use cases become more complex, these classes are likely to diverge and evolve differently in each service.

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


### Adding a Review to a Product

This use case can be implemented in several ways, each with its own trade-offs in terms of responsibility, coupling, and data consistency. 
Below are three possible approaches:

#### **Approach 1: Front-End Service as Orchestrator (Current Implementation)**
In this version, the `front-product-service` acts as the orchestrator for adding a review to a product. Here’s how it works:
1. A client sends a **POST request** to the `front-product-service` with the product ID and review details.
2. The `front-product-service` first retrieves the product information from the `product-service` to validate the product ID.
    - If the product exists, it sends a **POST request** to the `review-service` to add the review.
    - If the product does not exist, it returns a **404 Not Found** error to the client.

**Key Characteristics**:
- The `front-product-service` is responsible for validating the product ID and coordinating the addition of the review.
- This approach centralizes the orchestration logic in the front-end service, reducing coupling between the `review-service` and `product-service`.
- However, it introduces additional latency due to the extra call to the `product-service`.

#### **Approach 2: Product Service as Orchestrator**
An alternative approach would be to delegate the validation and orchestration to the `product-service`. Here’s how it would work:
1. A client sends a **POST request** to the `front-product-service` with the product ID and review details.
2. The `front-product-service` sends a **POST request** to the `product-service` with the product ID and review details.
3. The `product-service` validates the product ID.
    - If the product exists, it sends a **POST request** to the `review-service` with the review details.
    - If the product does not exist, it returns a **404 Not Found** error.

**Key Characteristics**:
- The `product-service` becomes the orchestrator, taking on the responsibility of validating the product ID.
- This approach creates a **short chain of calls** between services, increasing coupling between the `product-service` and `review-service`.
- While it simplifies the role of the `front-product-service`, it is generally not recommended in real-world scenarios due to the tighter coupling 
and reduced separation of concerns.

#### **Approach 3: No Validation (Eventual Consistency)**
A third approach is to skip product ID validation entirely. Here’s how it would work:
1. A client sends a **POST request** to the `front-product-service` with the product ID and review details.
1. The `front-product-service` sends a **POST request** to the `review-service` with the product ID and review details.
2. The `review-service` adds the review to the database without validating the product ID.
    - If the product does not exist, the review becomes **orphaned** (i.e., it is stored without a valid product reference).
    - The application continues to function smoothly, since the review is never retrieved with a product.

**Key Characteristics**:
- This approach eliminates the need for a call to the `product-service`, making the system **more efficient and resilient** to failures.
- However, it introduces **data inconsistency**, as orphaned reviews may accumulate in the database.
- To address this, a **batch process** could periodically clean up orphaned reviews, ensuring eventual consistency.



#### Comparison 
| **Approach**                  | **Pros**                                                                 | **Cons**                                                                 |
|-------------------------------|--------------------------------------------------------------------------|--------------------------------------------------------------------------|
| **Front-End Orchestration**    | Low coupling, clear separation of concerns                              | Additional latency, front-end service becomes a bottleneck              |
| **Review Service Orchestration**| Simplifies front-end service logic                                      | Higher coupling, reduced separation of concerns                         |
| **No Validation**              | High efficiency, resilience to failures                                 | Data inconsistency, requires additional cleanup processes               |


## References and Further Reading
This example is inspired by the book **"Microservices with Spring Boot 3 and Spring Cloud, Third Edition. Magnus Larson. Ed. Packt"** and its accompanying GitHub repository:
- [GitHub Repository](https://github.com/PacktPublishing/Microservices-with-Spring-Boot-and-Spring-Cloud-Third-Edition/tree/SB3.2)


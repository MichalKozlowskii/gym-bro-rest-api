# gym-bro-rest-api

## Description
A Spring Boot-based RESTful API backend for a gym mobile app.

## Features
* **Secured Endpoints:** All API endpoints are protected with Spring Security combined with JSON Web Tokens (JWT) for robust authentication and authorization.
* **Flexible Authentication:** Supports both traditional password-based authentication and GitHub login for seamless user access.
* **Exercise Management:** Enables the creation of exercise entities with the ability to attach demonstration video URLs, ensuring clear guidance.
* **Workout Plan Creation:** Allows users to create personalized workout plans tailored to their fitness goals.
* **Workout Functionality:** Provides comprehensive workout functionalities integrated within the API.
* **Stats Tracking:** Incorporates detailed tracking of exercise statistics to help users monitor progress and improve their performance.
* **Comprehensive Testing:** The application is tested using a layered approach:
  * **Unit Tests** for business logic validation.
  * **Integration Tests** to verify the correct interaction between components.
  * **Web Layer Tests** to ensure API endpoints behave as expected.

## Dependencies used
* Spring Security
* OAuth2 client
* Spring Data JPA
* Spring Boot Web
* Spring Boot Validation
* Flyway Migrations
* MySQL
* H2 database (runtime)
* Project Lombok
* Mapstruct
* JJWT for JWT
* Spring doc for Swagger

## Documentation
soon

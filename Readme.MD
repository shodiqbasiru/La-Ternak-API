# LaTernak API
LaTernak is an online platform that provides a livestock trading service between sellers and buyers.
Sellers can sell their livestock products and buyers can search and choose the products they want.

LaTernak is a RESTful API built using Java and Spring Boot. The project provides a complete backend solution for an
online livestock trading platform. The API provides functionality for user authentication, product management, order
processing, and payment processing. The API is secured using JWT tokens and provides different access levels for
different types of users (Admin, Customers, and Sellers).

## Features

- RESTful API: The project provides a RESTful API for all functionality.
- User Authentication: Register and login functionality for all users.
- User Authorization: Different access levels for different types of users (Admin, Customers, and Sellers).
- Product Management: Sellers can create, update, delete, and view their livestock products.
- User Management: Admin can create, update, delete, and view all users.
- Image Management: Users can upload images for their products or their profile.
- Review System: Customers can leave reviews for products they have purchased.
- Order and Payment System: Customers can place orders for products and make payments. The system integrates with
  Midtrans for payment processing.
- Order History: Customers can view their past orders.
- User Profile: All users can view and update their profile information.
- Product Details: Customers can view detailed information about a product.
- Product Reviews: Customers can view reviews left by other users for a product.
- Pagination: Support for pagination in product and user listings.

## Technologies Used

- Java: The main programming language used in the project.
- Spring Boot: The framework used for creating the RESTful API.
- Spring Security: Used for authentication and authorization.
- Spring Data JPA: Used for database access and manipulation.
- PostgreSQL: The database used for storing all data.
- Maven: The build tool used for managing dependencies and building the project.
- Swagger OpenAPI: Used for API documentation.
- Jwt: Used for handling JSON Web Tokens for secure transmission of information.
- Midtrans: Payment gateway used for handling transactions.
- Lombok: Java library used to reduce boilerplate code.
- IntelliJ IDEA: The IDE used for development.
- Git: Version control system used for tracking changes and collaboration.
- GitHub: The platform used for hosting the project and collaboration.
- JUnit : A simple framework to write repeatable tests in Java.
- Mockito : A mocking framework used in conjunction with JUnit for unit testing in Java.

## Setup and Installation

1. **Clone the Repository**

   First, clone this repository to your local machine using `git`:

    ```bash
    git clone https://github.com/shodiqbasiru/La-Ternak-API.git
    ```

2. **Install Dependencies**

   Navigate to the project directory and install the necessary dependencies:

    ```bash
    cd La-Ternak-API
    mvn install
    ```

3. **Set up the Database**

   You will need to have PostgreSQL installed on your machine. Once installed, create a new database:

    ```sql
    CREATE DATABASE laternak;
    ```

   Update the `application.properties` file in the `src/main/resources` directory with your database credentials.

4. **Run the Application**

   You can run the application using the following command:

    ```bash
    mvn spring-boot:run
    ```

   The application will be accessible at `http://localhost:8080`.

5. **Access the API Documentation**

   You can access the API documentation by visiting `http://localhost:8080/swagger-ui/index.html`.

## Usage

The API exposes the following endpoints:

### Auth

- `GET /api/auth/login`: Logs in a user and returns a JWT token.
   ```json
   {
      "username": "string",
      "password": "string"
   }
   ```
- `GET /api/auth/login-seller`: Logs in a seller and returns a JWT token.
   ```json
   {
      "username": "string",
      "password": "string"
   }
   ```
- `POST /api/auth/register`: Registers a new user.
   ```json
   {
      "name": "string",
      "phoneNumber": "string",
      "username": "string",
      "password": "string"
   }

   ```
- `POST /api/auth/register-seller`: Registers a new seller.
   ```json
   {
      "storeName": "string",
      "email": "string",
      "address": "string"
   }
   ```

- `GET /api/auth/verify-account-seller`: Verify the email account of the seller.
- `GET /api/auth/validate-token`: Validates a JWT token.
- `GET /api/auth/regenerate-top`: Regenerate the otp for verify the email account.

### User

- `GET /api/users`: Returns a list of all users.
- `GET /api/users/{id}`: Returns a user by ID.
- `PUT /api/users`: Updates a user.
   ```json
   {
        "id": "string",
        "customerName": "string",
        "phoneNumber": "string",
        "address": "string"
   }
   ```
- `DELETE /api/users/{id}`: Deletes a users by ID.

### Store
- `GET /api/store`: Returns a list of all stores
- `GET /api/stores/{id}`: Returns a store by ID.
- `PUT /api/stores`: Updates a store.
   ```json
   {
        "id": "string",
        "storeName": "string",
        "address": "string",
        "image": "multipart/form-data"
   }
   ```
- `DELETE /api/stores/{id}`: Delete a store by ID.


### Product

- `GET /api/products`: Returns a list of all products.
- `GET /api/products/pagination`: Returns a list of all products with pagination.
- `GET /api/products/{id}`: Returns a product by ID.
- `POST /api/products`: Creates a new product.
   ```json
   {
        "productName": "productName_681b27b87a70",
        "price": 0,
        "stock": 0,
        "description": "description_f91b1b16795b",
        "storeId": "storeId_f440fc3f6f06",
        "images": "List<MultipartFile>"
   }
   ```
- `PUT /api/products`: Updates a product by ID.
   ```json
   {
        "id": "string",
        "productName": "productName_681b27b87a70",
        "price": 0,
        "stock": 0,
        "description": "description_f91b1b16795b",
        "storeId": "storeId_f440fc3f6f06",
        "images": "List<MultipartFile>"
   }
   ```
- `DELETE /api/products/{id}`: Delete a product by ID.

### Cart

- `GET /api/carts`: Returns a list of all carts.
- `POST /api/carts`: Creates a new table.
   ```json
   {
      "productId": "string",
      "userId": "string",
      "qty": 0
   }
   ```
- `DELETE /api/carts/{id}`: Delete a cart by ID.

### Review

- `GET /api/reviews`: Returns a list of all reviews.
- `POST /api/reviews`: Creates a new review.
   ```json
   {
      "rating": 0,
      "comment": "string",
      "userId": "string",
      "productId": "string"
   }
    ```
- `DELETE /api/reviews/{id}`: Delete a review by ID.

### Order

- `GET /api/orders`: Returns a list of all orders.
- `GET /api/orders/mobile`: Returns a list of all orders for endpoint mobile.
- `GET /api/orders/{id}`: Returns an order by ID.
- `GET /api/orders/order-status/{id}`: Returns an order by status.,
- `POST /api/orders`: Creates a new order.
    ```json
    {
      "userId": "string",
      "orderDetailRequests": [
        {
          "qty": 0,
          "productId": "string"
        }
      ]
    }
    ```
- `POST /api/orders/status`: Update the status of order. this endpoint used by midtrans.
   

### Image User

- `GET /api/users/images/{imageId}`: Returns download the image user by ID.

### Image Product

- `GET /api/products/images/{imageId}`: Returns download the image product by ID.

### Image Store

- `GET /api/stores/images/{imageId}`: Returns download the image store by ID.
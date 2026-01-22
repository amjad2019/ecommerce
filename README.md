# ecommerce
ecommerce mini project with swagger, java Spring boot, postgresql using CQRS pattern

Spring Boot 3 application for product, user, and order management with dynamic discounts.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 14+ (or H2 for dev)

### Setup & Run
```bash
# 1. Clone & setup
git clone https://github.com/yourusername/ecommerce.git
cd ecommerce

# 2. Configure database
createdb shop

# 3. Update application.properties
# Set your DB credentials in src/main/resources/application.properties

# 4. Build & run
mvn clean package
java -jar target/ecommerce-*.jar

# OR with Maven
mvn spring-boot:run
```

##  Key Features
- **CQRS Architecture** - Clean separation of read/write operations
-  **Dynamic Discounts** - Strategy pattern for user/order-based discounts
-  **Stock Validation** - Real-time inventory checks
-  **Soft Delete** - Products with boolean deletion flag
-  **JWT Security** - Role-based authentication (USER/PREMIUM/ADMIN)
-  **Swagger UI** - Complete API documentation

##  Database Schema

### Main Tables SQL
```sql
CREATE SCHEMA IF NOT EXISTS shop;
SET search_path TO shop;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);


CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    quantity INT NOT NULL CHECK (quantity >= 0),
    deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_available ON products(quantity);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_total NUMERIC(14,2) NOT NULL CHECK (order_total >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_created ON orders(created_at);


CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
    discount_applied NUMERIC(12,2) DEFAULT 0 CHECK (discount_applied >= 0),
    total_price NUMERIC(14,2) NOT NULL CHECK (total_price >= 0),

    CONSTRAINT fk_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_items_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);


INSERT INTO shop.users (username, password, role)
VALUES
('admin', 'admin123', 'ADMIN'),
('user1', 'user123', 'USER'),
('premium1', 'premium123', 'PREMIUM_USER');


INSERT INTO shop.products (name, description, price, quantity, deleted)
VALUES
('iPhone 17 Pro Max', 'Latest Apple smartphone with A17 chip', 1599.99, 50, false),
('Samsung Galaxy S25', 'Flagship Samsung phone with 200MP camera', 1299.99, 30, false),
('MacBook Pro 16"', 'Apple laptop with M3 chip', 2499.99, 20, false),
('Sony WH-1000XM5', 'Noise cancelling wireless headphones', 399.99, 100, false);
```

## Discount Rules
| User Type | Base Discount | Bulk Discount (>$500) |
|-----------|---------------|----------------------|
| USER | 0% | +5% |
| PREMIUM_USER | 10% | +5% |
| ADMIN | Custom | +5% |

## API Endpoints

### Authentication
```http
POST /api/auth/register
POST /api/auth/login
```

### Products (CQRS)
```http
# Command Side (Write)
POST   /api/products              # Create
PUT    /api/products/{id}         # Update
DELETE /api/products/{id}         # Soft delete

# Query Side (Read)
GET    /api/products              # List with pagination
GET    /api/products/{id}         # Get by ID
GET    /api/products/search       # Search by name/price/availability
```

### Orders
```http
POST   /api/orders                # Create order (validates stock)
GET    /api/orders/{id}           # Get order details
GET    /api/orders/my-orders      # Current user orders
PATCH  /api/orders/{id}/status    # Update status (ADMIN)
POST   /api/orders/{id}/cancel    # Cancel order
```

## Configuration

### application.properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/shop
spring.datasource.username=postgres
spring.datasource.password=yourpassword

# JWT
app.jwt.secret=your-256-bit-secret
app.jwt.expiration=86400000

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## Access Points
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs


## Sample Requests
```bash
# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'
```

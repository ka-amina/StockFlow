# StockFlow - Logistics Management API

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Business Rules](#business-rules)
- [User Roles](#user-roles)
- [Data Model](#data-model)
- [Testing](#testing)
- [Contributing](#contributing)

## 🎯 Overview

StockFlow is a modular REST API designed for comprehensive logistics management. The application enables management of products (SKU), multi-warehouse inventory, customer orders, supplier procurement, shipments, and delivery tracking.

### Key Objectives
- Complete traceability of all logistics operations
- Strict stock control (no negative stock allowed)
- Automation of critical business rules (reservation, shipment, reception)
- Multi-warehouse inventory management
- Advanced order fulfillment with backorder support

## ✨ Features

### Product & Inventory Management
- Complete product lifecycle (CRUD operations)
- Multi-warehouse inventory tracking
- Real-time stock availability calculation: `available = qtyOnHand - qtyReserved`
- Inventory movements tracking (INBOUND, OUTBOUND, ADJUSTMENT)
- Stock reservation system

### Supplier & Procurement
- Supplier management
- Purchase Order creation and tracking
- Partial or complete order reception
- Automatic stock updates upon reception

### Customer Orders
- Sales Order creation with multiple line items
- Order lifecycle: `CREATED → RESERVED → SHIPPED → DELIVERED → CANCELED`
- Mandatory reservation before shipment
- Automatic backorder creation for insufficient stock
- Multi-warehouse allocation support

### Shipment & Tracking
- Shipment creation linked to orders
- Carrier and tracking number assignment
- Shipment status tracking: `PLANNED → IN_TRANSIT → DELIVERED`
- Logistics cut-off time enforcement (3 PM)

### Reporting & Search
- Filtering by date, status, or movement type
- Basic statistics (order count, delivery rate, stockout detection)

## 🏗️ Architecture

The application follows a clean layered architecture:

```
┌─────────────────────┐
│   Controller Layer  │  ← REST endpoints, validation (@Valid)
├─────────────────────┤
│    Service Layer    │  ← Business logic, rules enforcement
├─────────────────────┤
│  Repository Layer   │  ← Data access (Spring Data JPA)
├─────────────────────┤
│   Database Layer    │  ← PostgreSQL
└─────────────────────┘
```

### Key Components
- **Controllers**: Handle HTTP requests and validation
- **Services**: Implement business rules and orchestrate operations
- **Repositories**: Manage data persistence
- **DTOs**: Data transfer objects for API communication
- **Mappers**: Entity ↔ DTO conversion using MapStruct
- **Global Exception Handler**: Centralized error management

## 🛠️ Technologies

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Database**: PostgreSQL (with H2 for development)
- **ORM**: Spring Data JPA / Hibernate
- **Mapping**: MapStruct 1.6.3
- **Validation**: Jakarta Bean Validation
- **Documentation**: Swagger/OpenAPI (planned)
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven

## 🚀 Getting Started

### Prerequisites
- Java 17 (JDK 17.0.17 or compatible)
- PostgreSQL 12+ (or use H2 for development)
- Maven 3.6+
- Docker & Docker Compose (optional, for containerized deployment)

### Installation

#### Option 1: Run with Docker (Recommended)
```bash
# Clone the repository
git clone https://github.com/ka-amina/StockFlow.git
cd StockFlow

# Start all services (app + PostgreSQL)
docker-compose up -d

# Application available at http://localhost:8080
```

#### Option 2: Run Locally

1. **Clone the repository**
   ```bash
   git clone https://github.com/ka-amina/StockFlow.git
   cd StockFlow
   ```

2. **Configure the database**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   # PostgreSQL configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/stockflow_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # Or use H2 for development
   # spring.datasource.url=jdbc:h2:mem:stockflow
   ```

3. **Build the project**
   ```bash
   # Use Java 17 explicitly
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)  # macOS
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   # Using Maven
   ./mvnw spring-boot:run
   
   # Or run the JAR
   java -jar target/stockflow-0.0.1-SNAPSHOT.jar
   ```

5. **Access the application**
   - API: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

### 🐳 Docker Deployment

The application is fully containerized and automatically built in CI/CD:

```bash
# Pull latest image from GitHub Container Registry
docker pull ghcr.io/ka-amina/stockflow:latest

# Run with docker-compose (includes PostgreSQL)
docker-compose up -d

# Or run standalone container
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/stockflow_db \
  ghcr.io/ka-amina/stockflow:latest
```

**📖 Full Docker Documentation**: [docs/DOCKER_INTEGRATION.md](./docs/DOCKER_INTEGRATION.md)

## 📚 API Documentation

### Main Endpoints

#### Products
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

#### Warehouses
- `GET /api/warehouses` - List all warehouses
- `POST /api/warehouses` - Create warehouse
- `PUT /api/warehouses/{id}` - Update warehouse

#### Purchase Orders
- `GET /api/purchase-orders` - List purchase orders
- `POST /api/purchase-orders` - Create purchase order
- `POST /api/purchase-orders/receive` - Receive items
- `PUT /api/purchase-orders/{id}/cancel` - Cancel order

#### Sales Orders (Planned)
- `GET /api/sales-orders` - List customer orders
- `POST /api/sales-orders` - Create order
- `POST /api/sales-orders/{id}/reserve` - Reserve stock
- `POST /api/sales-orders/{id}/ship` - Create shipment
- `PUT /api/sales-orders/{id}/cancel` - Cancel order

#### Inventory (Planned)
- `GET /api/inventory` - View inventory levels
- `POST /api/inventory/movements` - Record movement
- `POST /api/inventory/adjust` - Adjust stock

## 📜 Business Rules

### Critical Rules

1. **No Negative Stock**: All outbound operations verify availability before execution
2. **Mandatory Reservation**: Orders can only be shipped after successful reservation
3. **Multi-Warehouse Allocation**: System distributes across warehouses based on priority
4. **Automatic Backorders**: Created for unavailable quantities
5. **Cut-off Time**: Orders after 3 PM are scheduled for next business day
6. **Reservation TTL**: Reservations expire after 24h if not confirmed
7. **Shipment Capacity**: Respects maximum capacity per time slot

### Stock Calculation
```
Available Stock = qtyOnHand - qtyReserved
```

### Order Lifecycle
```
CREATED → RESERVED → SHIPPED → DELIVERED
    ↓
CANCELED (only before SHIPPED)
```

### Purchase Order Lifecycle
```
ISSUED → PARTIALLY_RECEIVED → FULLY_RECEIVED
    ↓
CANCELED (only if not received)
```

## 👥 User Roles

### ADMIN
- Manage users, products, warehouses
- Create and manage purchase orders
- Configure global settings
- Cancel orders at any stage (before shipment)

### WAREHOUSE_MANAGER
- Manage inventory and movements
- Handle reservations and shipments
- Record inbound/outbound operations
- Process order fulfillment

### CLIENT
- Create and view orders
- Track shipments
- View order history
- Check product availability

## 💾 Data Model

### Core Entities

```
User (id, email, passwordHash, role, active)
Product (id, sku, name, category, active)
Warehouse (id, code, name, active)
Inventory (id, product, warehouse, qtyOnHand, qtyReserved)
InventoryMovement (id, product, warehouse, type, quantity, occurredAt)
Supplier (id, name, contactEmail, contactPhone, active)
PurchaseOrder (id, supplier, poNumber, status, issuedDate, expectedDeliveryDate)
PurchaseOrderItem (id, purchaseOrder, product, quantityOrdered, quantityReceived, price)
Client (id, name, email) [Planned]
SalesOrder (id, client, warehouse, status, orderDate) [Planned]
SalesOrderLine (id, salesOrder, product, quantity, unitPrice) [Planned]
Shipment (id, salesOrder, carrier, trackingNumber, status) [Planned]
```

## 🧪 Testing

### Run Tests
```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

### Test Coverage Areas
- Stock availability validation
- Reservation and release operations
- Order status transitions
- Backorder creation
- Cut-off time enforcement
- Shipment planning
- Business exceptions
- DTO validation
- MapStruct mappers

## 🔧 Configuration

Key configuration properties in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/stockflow_db
spring.jpa.hibernate.ddl-auto=update

# Logging
logging.level.com.example.demo=DEBUG

# Business Rules
logistics.cutoff.hour=15
logistics.reservation.ttl.hours=24
```

## 🐛 Error Handling

All errors return standardized JSON responses:

```json
{
  "timestamp": "2025-11-14T11:00:00Z",
  "status": 400,
  "message": "Cannot receive more than ordered quantity",
  "path": "/api/purchase-orders/receive"
}
```

### Exception Types
- `ResourceNotFoundException` (404) - Resource not found
- `BusinessException` (400) - Business rule violation
- `ValidationException` (400) - Input validation error
- `StockUnavailableException` (409) - Insufficient stock
- `GenericException` (500) - Unexpected error

## 📝 Development Guidelines

### Code Style
- Follow Java naming conventions
- Use Lombok to reduce boilerplate
- Keep business logic in service layer
- Validate all inputs with Bean Validation
- Write meaningful commit messages

### Git Workflow
```bash
# Create feature branch
git checkout -b SF-XX-feature-name

# Commit changes
git add .
git commit -m "SF-XX: Description of changes"

# Push and create PR
git push origin SF-XX-feature-name
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b SF-XX-feature`)
3. Commit your changes (`git commit -m 'SF-XX: Add feature'`)
4. Push to the branch (`git push origin SF-XX-feature`)
5. Open a Pull Request

## 📄 License

This project is an educational project for learning Spring Boot and logistics management concepts.

## 📧 Contact

**Project Repository**: [StockFlow](https://github.com/ka-amina/StockFlow)

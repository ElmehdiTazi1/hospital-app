# Hospital Management System

## Overview

This application is a comprehensive hospital management system developed with Spring Boot. It utilizes a modular architecture to manage various aspects of hospital operations, including patients, doctors, departments, appointments, prescriptions, and medication inventory.

## Architecture

The system follows a multi-layered, modular architecture with the following components:

- **hospital-domain**: Contains domain entities representing core business objects
- **hospital-repository**: Implements data access layer using Spring Data JPA
- **hospital-service**: Business logic layer with service implementations
- **hospital-web**: Web interface with Thymeleaf templates
- **hospital-api**: REST API endpoints and DTOs
- **hospital-config**: Common configuration for all modules
- **hospital-commons**: Shared utilities and helpers

## Technologies

- **Backend**: Java 17, Spring Boot 3.2.4
- **Persistence**: Spring Data JPA, Hibernate
- **Frontend**: Thymeleaf, Bootstrap 5, Bootstrap Icons
- **API Documentation**: OpenAPI 3 (Swagger)
- **Build Tool**: Maven
- **Database**: In-memory H2 database (configurable for production environments)

## Features

### Patient Management
- Patient registration and medical records
- Search functionality with pagination
- Patient status tracking (health scores, condition)

### Doctor Management
- Doctor profiles with specialties
- Availability status tracking
- Assignment to departments

### Department Management
- Department creation and maintenance
- Department head assignment
- Tracking bed capacity

### Appointment System
- Scheduling and managing appointments
- Status tracking (planned, confirmed, canceled, completed)
- Filtering by doctor, patient, date

### Medication Inventory
- Medication stock tracking
- Stock alerts for low inventory
- Expiration date management

### Prescription Management
- Creation of prescriptions with medication dosages
- Prescription validity period
- Status tracking (active, completed, canceled)

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/hospital-management.git
   ```

2. Navigate to the project directory:
   ```bash
   cd hospital-management
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   cd hospital-web
   mvn spring-boot:run
   ```

5. Access the web interface:
   - Web UI: [http://localhost:8080](http://localhost:8080)
   - API Documentation: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Project Structure

```
hospital-app/
├── hospital-api/             # REST API layer
├── hospital-commons/         # Common utilities
├── hospital-config/          # Configuration
├── hospital-domain/          # Domain entities
├── hospital-repository/      # Data access layer
├── hospital-service/         # Business logic layer
└── hospital-web/             # Web interface
```

## API Endpoints

The system provides a RESTful API, documented with OpenAPI:

- `/api/patients` - Patient management
- `/api/medecins` - Doctor management
- `/api/departements` - Department management
- `/api/rendezvous` - Appointment management
- `/api/medicaments` - Medication inventory
- `/api/prescriptions` - Prescription management

## License

This project is licensed under the Apache 2.0 License - see the LICENSE file for details.

## Acknowledgments

- Spring Framework Team
- Bootstrap Team

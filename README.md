# firestick

A Spring Boot application for code analysis, embeddings, and search capabilities.

## Technologies

### Backend
- **Java 21** - Latest LTS version of Java
- **Spring Boot 3.5.6** - Spring Boot framework with embedded Tomcat
- **Maven** - Build and dependency management

### Code Analysis
- **JavaParser 3.26.3** - Java source code parsing and AST analysis
- **JGraphT 1.5.2** - Graph library for dependency analysis
- **Apache Lucene 9.12.0** - Full-text search and indexing

### Data & Embeddings
- **ONNX Runtime 1.20.0** - Machine learning inference for Sentence-Transformers
- **DJL 0.30.0** - Deep Java Library for ML integration
- **H2 Database** - Embedded SQL database

### Vector Store
- **Chroma** - Vector database (manual installation required)

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/firestick-1.0.0-SNAPSHOT.jar
```

Or use Maven:

```bash
mvn spring-boot:run
```

### Access the Application

- **API Health Check**: http://localhost:8080/api/health
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:firestickdb`
  - Username: `sa`
  - Password: (empty)

## Features

### Code Parser Service
Parse Java source code and analyze AST using JavaParser.

### Dependency Graph Service
Create and analyze dependency graphs using JGraphT.

### Code Search Service
Index and search code using Apache Lucene.

## Project Structure

```
firestick/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/caveanimal/firestick/
│   │   │       ├── FirestickApplication.java
│   │   │       ├── controller/
│   │   │       │   └── HealthController.java
│   │   │       └── service/
│   │   │           ├── CodeParserService.java
│   │   │           ├── CodeSearchService.java
│   │   │           └── DependencyGraphService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/caveanimal/firestick/
│               └── FirestickApplicationTests.java
└── pom.xml
```

## Configuration

The application can be configured via `application.properties`:

- Server port: `server.port=8080`
- H2 Database settings
- JPA/Hibernate settings

## Testing

```bash
mvn test
```

## Notes

- The Chroma Java client requires manual installation as it's not available in Maven Central
- ONNX Runtime is configured for embeddings support via Sentence-Transformers
- DJL provides additional ML capabilities

## License

This project is for demonstration purposes.

# Firestick - Legacy Code Analysis and Search Tool

## Overview
Firestick is a powerful desktop application for analyzing and searching legacy codebases using semantic search, code metrics, and dependency analysis.

## Features
- 🔍 Semantic code search with AI embeddings
- 📊 Code complexity analysis
- 🕸️ Dependency graph visualization
- 💡 Code smell detection
- 🎯 Dead code identification
- 🖥️ Modern web-based UI

## System Requirements
- Java 21+
- Maven 3.8+
- Node.js 18+ (for UI development)
- 4GB RAM recommended

## Quick Start

### Build and Run
```bash
# Clone repository
git clone https://github.com/CaveAnimal/firestick.git
cd firestick

# Build backend
mvn clean package

# Run application
mvn spring-boot:run

# Access UI at http://localhost:8080
```

### Development Setup
See [DEVELOPMENT.md](docs/DEVELOPMENT.md) for detailed setup instructions.

## Documentation
- [User Guide](docs/USER_GUIDE.md)
- [Developer Guide](docs/DEVELOPER_GUIDE.md)
- [API Documentation](docs/API.md)
- [Architecture](docs/ARCHITECTURE.md)

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md)

## License
Distributed under the MIT License. See [LICENSE](LICENSE) for details.

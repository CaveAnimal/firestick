# Firestick User Guide

Welcome to Firestick! This guide will help you get started, use key features, and troubleshoot common issues.

## Getting Started
- Install Java 21+, Maven 3.8+, Node.js 18+
- Build and run the backend: `mvn clean package && mvn spring-boot:run`
- Access the UI at http://localhost:8080

## Features
- Semantic code search
- Code complexity analysis
- Dependency graph visualization
- Code smell detection
- Dead code identification

## Troubleshooting
- Check logs in `target/`
- For database issues, verify H2 settings in `application.properties`
- For UI issues, ensure Node.js is installed

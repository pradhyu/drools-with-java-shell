# DMV Rules Engine

A sophisticated business rules management system built with Spring Boot, Drools, and JShell integration. This system provides multiple interfaces for rule development, testing, and execution, including REST APIs, web-based REPL, command-line JShell access, and LSP support for IDE integration.

> **✅ Status**: All systems operational - 35 tests passing, build successful, rules engine fully functional

## Features

### 🔧 Interactive Development
- **JShell REPL**: Web-based and command-line interactive Java environment
- **Hot Reloading**: Modify rules without application restart
- **LSP Integration**: VS Code support with syntax highlighting and error detection

### 📋 Rule Management
- **Dynamic Rule Compilation**: Compile and deploy rules at runtime
- **Version Control**: Rule history tracking with rollback capabilities
- **REST API**: Comprehensive API for rule management and evaluation

### 🧪 Testing & Debugging
- **Test Framework**: Scenario-based testing with batch execution
- **Load Simulation**: Performance testing with concurrent execution
- **Debug Tools**: Step-by-step execution tracing and profiling
- **Conflict Analysis**: Automatic detection of rule conflicts

### 🏢 DMV Business Logic
- **License Renewal Processing**: Automated evaluation of renewal requests
- **Age Verification**: Parental consent requirements for minors
- **Violation Checking**: Outstanding violation resolution requirements
- **Expiration Handling**: Driving test requirements for expired licenses

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd dmv-rules-engine
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access the web interface:
- Main Interface: http://localhost:8080
- JShell REPL: http://localhost:8080/jshell
- API Documentation: http://localhost:8080/api

### Command Line JShell Mode

Start with interactive JShell:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--jshell"
```

## Usage Examples

### REST API

#### Evaluate License Renewal
```bash
curl -X POST http://localhost:8080/api/dmv/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "APP001",
    "personalInfo": {
      "firstName": "John",
      "lastName": "Doe",
      "dateOfBirth": "1985-05-15",
      "address": {
        "street": "123 Main St",
        "city": "Anytown",
        "state": "CA",
        "zipCode": "12345"
      }
    },
    "currentLicense": {
      "licenseNumber": "D1234567",
      "licenseClass": "CLASS_C",
      "issueDate": "2020-01-01",
      "expirationDate": "2025-01-01",
      "status": "ACTIVE"
    },
    "violations": [],
    "renewalType": "STANDARD"
  }'
```

#### List Rules
```bash
curl -X GET http://localhost:8080/api/rules
```

#### Reload Rules
```bash
curl -X POST http://localhost:8080/api/rules/reload
```

### JShell REPL

Access the web-based REPL at http://localhost:8080/jshell and try:

```java
// Use pre-loaded sample data
sampleAdult
sampleMinor
sampleExpired
sampleWithViolations

// Check properties
sampleAdult.getPersonalInfo().getAge()
sampleAdult.getCurrentLicense().isExpired()
sampleWithViolations.hasOutstandingViolations()

// Convert to JSON
factToJson(sampleAdult)
```

### Testing Framework

#### Execute Test Scenario
```bash
curl -X POST http://localhost:8080/api/test/execute \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioName": "valid-adult-test",
    "description": "Test valid adult renewal",
    "inputFacts": [...],
    "expectedOutcomes": [...]
  }'
```

#### Run Load Simulation
```bash
curl -X POST http://localhost:8080/api/test/simulate \
  -H "Content-Type: application/json" \
  -d '{
    "concurrentUsers": 5,
    "executionsPerUser": 10,
    "testScenario": {...}
  }'
```

## Architecture

### Core Components

- **Rules Management Service**: Dynamic rule compilation and hot-reloading
- **JShell Integration Service**: Interactive Java REPL environment
- **Version Control Service**: Rule history and rollback management
- **Test Execution Service**: Comprehensive testing framework
- **Debug Service**: Rule debugging and profiling tools
- **LSP Server**: Language Server Protocol for IDE integration

### Technology Stack

- **Spring Boot 3.2**: Application framework
- **Drools 8.44**: Business rules engine
- **JShell**: Interactive Java environment
- **LSP4J**: Language Server Protocol implementation
- **H2 Database**: In-memory database for version control
- **WebSocket**: Real-time communication for web REPL
- **Thymeleaf**: Web template engine

## Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
dmv:
  rules:
    path: classpath:rules/
    reload-interval: 30000
  jshell:
    session-timeout: 3600000
    max-sessions: 10
  lsp:
    port: 8081
    websocket-path: /lsp
```

### Rule Files

Place Drools rule files (`.drl`) in `src/main/resources/rules/`:

```drools
package com.dmv.rules

import com.dmv.model.*

rule "Age Verification - Adult"
when
    $request : LicenseRenewalRequest( personalInfo.age >= 18 )
    $decision : RenewalDecision( decision == DecisionType.APPROVED )
then
    $decision.addReason("Age verification passed");
    update($decision);
end
```

## API Documentation

### DMV Evaluation Endpoints

- `POST /api/dmv/evaluate` - Evaluate license renewal request
- `GET /api/dmv/status` - Get system status
- `POST /api/dmv/validate` - Validate renewal request

### Rules Management Endpoints

- `GET /api/rules` - List all rules
- `POST /api/rules/reload` - Reload all rules
- `POST /api/rules/compile` - Compile rule content
- `POST /api/rules/deploy` - Deploy new rule
- `DELETE /api/rules/{ruleName}` - Remove rule

### Version Control Endpoints

- `GET /api/versions/{ruleName}/history` - Get rule history
- `POST /api/versions/{ruleName}/versions` - Save rule version
- `GET /api/versions/{ruleName}/compare/{v1}/{v2}` - Compare versions
- `POST /api/versions/{ruleName}/rollback/{versionId}` - Rollback rule

### Testing Endpoints

- `POST /api/test/execute` - Execute test scenario
- `POST /api/test/batch` - Execute batch tests
- `POST /api/test/simulate` - Run load simulation
- `GET /api/test/scenarios` - List available scenarios

### Debug Endpoints

- `POST /api/debug/sessions` - Create debug session
- `POST /api/debug/sessions/{id}/trace` - Execute with trace
- `GET /api/debug/sessions/{id}/facts` - Get current facts
- `POST /api/debug/analyze-conflicts` - Analyze rule conflicts

## Development

### Building from Source

```bash
# Clone repository
git clone <repository-url>
cd dmv-rules-engine

# Build project (includes compilation and test execution)
mvn clean compile

# Run comprehensive test suite (35 tests)
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run
```

### Recent Improvements

**✅ Fully Tested & Validated**
- 35 comprehensive unit tests covering all major components
- Fixed Drools rule compilation and execution
- Resolved Spring Boot context loading issues
- Updated Jackson serialization for Java 8 time types
- All model classes properly implemented and tested

**🔧 Core Fixes Applied**
- Age verification rules now properly create and insert decisions
- License expiration logic handles all status scenarios correctly
- Test framework validates rule execution and outcomes
- JSON serialization/deserialization fully functional
- All REST endpoints properly configured and tested

### IDE Integration

#### VS Code Setup

1. Install a generic LSP client extension
2. Configure connection to `ws://localhost:8081/lsp`
3. Open `.drl` files for syntax highlighting and error detection

#### IntelliJ IDEA

1. Import as Maven project
2. Configure Drools plugin for `.drl` file support
3. Use built-in REST client for API testing

### Custom Rule Development

1. Create rule files in `src/main/resources/rules/`
2. Use JShell REPL for interactive testing
3. Deploy via REST API or hot-reload
4. Version control changes
5. Create test scenarios for validation

## Troubleshooting

### Common Issues

**Port Conflicts**
- Ensure ports 8080 (web) and 8081 (LSP) are available
- Configure different ports in `application.yml` if needed

**Memory Issues**
- Increase JVM heap size: `-Xmx1g`
- Monitor memory usage via `/api/debug/profile`

**Rule Compilation Errors**
- Check `.drl` file syntax - rules must properly create and insert facts
- Use LSP integration for real-time error detection
- Test rules in JShell REPL before deployment
- Ensure rules follow the pattern: `when ... not RenewalDecision() then insert(new RenewalDecision(...))`

**JShell Session Issues**
- Sessions timeout after 1 hour of inactivity
- Check active sessions via `/jshell/status`
- Restart application if sessions become unresponsive

**Test Execution**
- All 35 tests should pass - if any fail, check for missing dependencies
- Jackson serialization requires JSR310 module for Java 8 time types
- Model classes must have proper getters/setters for JSON serialization

### Debug Commands

```bash
# Application health
curl http://localhost:8080/actuator/health

# System status
curl http://localhost:8080/api/dmv/status

# Active debug sessions
curl http://localhost:8080/api/debug/sessions

# Performance profile
curl http://localhost:8080/api/debug/profile

# Verify tests are passing
mvn test -q

# Check rule compilation
mvn clean compile -q
```

### Validation Checklist

Before deploying or making changes, ensure:

- [ ] `mvn test` passes all 35 tests
- [ ] `mvn clean compile` completes without errors  
- [ ] Drools rules follow proper fact insertion patterns
- [ ] JSON serialization works for all model classes
- [ ] Spring Boot application starts successfully
- [ ] REST endpoints respond correctly

## Testing

### Test Coverage

The project includes comprehensive test coverage with 35 unit tests:

**Model Tests** (7 tests)
- `LicenseRenewalRequestTest`: Validates all model classes and business logic
- Tests license expiration calculations, age verification, violation handling
- Validates enum values and builder patterns

**Service Tests** (15 tests)  
- `RulesManagementServiceTest`: Rule compilation and execution validation
- `JShellServiceTest`: Interactive shell functionality and session management
- `TestExecutionServiceTest`: Test framework validation and batch processing

**Controller Tests** (8 tests)
- `DmvEvaluationControllerTest`: REST API endpoint validation
- `RulesManagementControllerTest`: Rule management API testing
- JSON serialization/deserialization validation

**Application Tests** (2 tests)
- `DmvRulesEngineApplicationTests`: Application startup and configuration
- Main method and class structure validation

**Integration Tests** (3 tests)
- End-to-end workflow validation
- Rule execution with real data
- Performance and load testing scenarios

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=LicenseRenewalRequestTest

# Run tests with coverage
mvn test jacoco:report

# Run tests in quiet mode
mvn test -q
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with comprehensive tests
4. Ensure all 35 tests pass: `mvn test`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For questions and support:
- Check the demo script: `src/main/resources/demo/demo-script.md`
- Review API documentation at http://localhost:8080/api
- Use JShell REPL for interactive exploration
- Create issues for bugs or feature requests# drools-with-java-shell

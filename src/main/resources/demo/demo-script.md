# DMV Rules Engine Demo Script

This document provides a comprehensive demonstration of the DMV Rules Engine capabilities.

## Prerequisites

1. Start the application: `mvn spring-boot:run`
2. Access the web interface at: http://localhost:8080
3. JShell REPL available at: http://localhost:8080/jshell
4. API documentation available at: http://localhost:8080/api

## Demo Scenarios

### 1. Basic Rule Evaluation

#### Test Valid Adult Renewal
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
      },
      "phoneNumber": "+1-555-123-4567"
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

Expected Result: APPROVED decision with automatic renewal

#### Test Minor Renewal
```bash
curl -X POST http://localhost:8080/api/dmv/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "APP002",
    "personalInfo": {
      "firstName": "Jane",
      "lastName": "Smith",
      "dateOfBirth": "2008-03-20",
      "address": {
        "street": "456 Oak Ave",
        "city": "Somewhere",
        "state": "CA",
        "zipCode": "54321"
      },
      "phoneNumber": "+1-555-987-6543"
    },
    "currentLicense": {
      "licenseNumber": "L9876543",
      "licenseClass": "LEARNER_PERMIT",
      "issueDate": "2023-06-01",
      "expirationDate": "2024-06-01",
      "status": "ACTIVE"
    },
    "violations": [],
    "renewalType": "STANDARD"
  }'
```

Expected Result: REQUIRES_ACTION with parental consent requirement

### 2. JShell Interactive Development

1. Open JShell REPL: http://localhost:8080/jshell
2. Try these commands:

```java
// Create a sample request
var request = sampleAdult;
request.getPersonalInfo().getAge()

// Check license status
request.getCurrentLicense().isExpired()

// Test with violations
var requestWithViolations = sampleWithViolations;
requestWithViolations.hasOutstandingViolations()

// Convert to JSON
factToJson(request)
```

### 3. Rule Management

#### List Current Rules
```bash
curl -X GET http://localhost:8080/api/rules
```

#### Reload Rules
```bash
curl -X POST http://localhost:8080/api/rules/reload
```

#### Compile New Rule
```bash
curl -X POST http://localhost:8080/api/rules/compile \
  -H "Content-Type: application/json" \
  -d '{
    "ruleContent": "package com.dmv.rules\nimport com.dmv.model.*\nrule \"Test Rule\"\nwhen\n    $request : LicenseRenewalRequest()\nthen\n    System.out.println(\"Test rule fired\");\nend"
  }'
```

### 4. Version Control

#### Save Rule Version
```bash
curl -X POST http://localhost:8080/api/versions/age-verification/versions \
  -H "Content-Type: application/json" \
  -d '{
    "content": "package com.dmv.rules\n...",
    "author": "demo-user",
    "commitMessage": "Updated age verification logic"
  }'
```

#### Get Rule History
```bash
curl -X GET http://localhost:8080/api/versions/age-verification/history
```

#### Compare Versions
```bash
curl -X GET http://localhost:8080/api/versions/age-verification/compare/v1/v2
```

### 5. Testing Framework

#### Execute Test Scenario
```bash
curl -X POST http://localhost:8080/api/test/execute \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioName": "valid-adult-test",
    "description": "Test valid adult renewal",
    "inputFacts": [
      {
        "applicantId": "TEST001",
        "personalInfo": {
          "firstName": "Test",
          "lastName": "User",
          "dateOfBirth": "1985-01-01",
          "address": {
            "street": "123 Test St",
            "city": "Test City",
            "state": "CA",
            "zipCode": "12345"
          }
        },
        "currentLicense": {
          "licenseNumber": "T1234567",
          "licenseClass": "CLASS_C",
          "issueDate": "2020-01-01",
          "expirationDate": "2025-01-01",
          "status": "ACTIVE"
        },
        "violations": [],
        "renewalType": "STANDARD"
      }
    ],
    "expectedOutcomes": [
      {
        "type": "RULE_FIRED",
        "description": "Age verification should fire",
        "expectedValue": "Age Verification - Adult Approved"
      }
    ]
  }'
```

#### Run Load Simulation
```bash
curl -X POST http://localhost:8080/api/test/simulate \
  -H "Content-Type: application/json" \
  -d '{
    "concurrentUsers": 5,
    "executionsPerUser": 10,
    "testScenario": {
      "scenarioName": "load-test",
      "inputFacts": [...],
      "expectedOutcomes": [...]
    }
  }'
```

### 6. Debugging and Profiling

#### Create Debug Session
```bash
curl -X POST http://localhost:8080/api/debug/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "sessionName": "demo-debug-session"
  }'
```

#### Execute with Trace
```bash
curl -X POST http://localhost:8080/api/debug/sessions/{sessionId}/trace \
  -H "Content-Type: application/json" \
  -d '[
    {
      "applicantId": "DEBUG001",
      "personalInfo": {...},
      "currentLicense": {...},
      "violations": [],
      "renewalType": "STANDARD"
    }
  ]'
```

#### Analyze Rule Conflicts
```bash
curl -X POST http://localhost:8080/api/debug/analyze-conflicts \
  -H "Content-Type: application/json" \
  -d '[
    {
      "applicantId": "CONFLICT001",
      "personalInfo": {...},
      "currentLicense": {...},
      "violations": [],
      "renewalType": "STANDARD"
    }
  ]'
```

### 7. LSP Integration (VS Code)

1. Install a generic LSP client extension in VS Code
2. Configure it to connect to: ws://localhost:8081/lsp
3. Open a .drl file and observe:
   - Syntax highlighting
   - Error detection
   - Code completion
   - Hover information

### 8. Command Line JShell

Start the application with JShell mode:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--jshell"
```

Try these commands in the interactive shell:
```java
// Access sample data
sampleAdult
sampleMinor
sampleExpired
sampleWithViolations

// Test rule execution
var decision = new RenewalDecision(DecisionType.APPROVED);
decision.addReason("Test reason");

// Check system status
/vars
/methods
/history
```

## Performance Benchmarks

### Expected Performance Metrics

- **Rule Evaluation**: < 50ms per request
- **JShell Code Execution**: < 100ms per command
- **Rule Compilation**: < 500ms per rule
- **Load Test**: 100+ requests/second
- **Memory Usage**: < 512MB for typical workload

### Monitoring Endpoints

- System Status: `GET /api/dmv/status`
- JShell Status: `GET /jshell/status`
- Performance Profile: `GET /api/debug/profile`
- Test Metrics: `GET /api/test/metrics`

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Ensure ports 8080 and 8081 are available
2. **Memory Issues**: Increase JVM heap size with `-Xmx1g`
3. **Rule Compilation Errors**: Check .drl file syntax
4. **JShell Session Timeout**: Sessions expire after 1 hour of inactivity

### Debug Commands

```bash
# Check application health
curl http://localhost:8080/actuator/health

# View loaded rules
curl http://localhost:8080/api/rules

# Check active debug sessions
curl http://localhost:8080/api/debug/sessions

# Get JShell status
curl http://localhost:8080/jshell/status
```

## Advanced Features Demo

### Custom Rule Development

1. Create a new rule file in `src/main/resources/rules/`
2. Use the LSP integration for syntax checking
3. Test in JShell REPL
4. Deploy via REST API
5. Version control the changes

### Integration Testing

1. Create comprehensive test scenarios
2. Run batch tests
3. Generate test reports
4. Analyze performance metrics
5. Set up continuous integration

This demo script showcases all major features of the DMV Rules Engine and provides a foundation for further development and customization.
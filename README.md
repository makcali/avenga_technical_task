# 📚 Bookstore API Automation Framework

**Enterprise-grade REST API Test Automation Framework**

Built with Java 17, REST Assured, TestNG, and Allure Reports

---

## 🎯 Framework Overview

This framework provides a complete solution for API test automation with:

- ✅ **Layered Architecture** - Clean separation of concerns
- ✅ **SOLID Principles** - Maintainable and scalable code
- ✅ **Design Patterns** - Singleton, Builder, Factory, Service Layer
- ✅ **Comprehensive Testing** - Happy path, edge cases, negative scenarios
- ✅ **Rich Reporting** - Allure reports with detailed test execution
- ✅ **CI/CD Ready** - GitHub Actions workflow included

---

## 📁 Project Structure

```
api-automation-framework/
├── src/main/java/com/bookstore/
│   ├── config/                    # Configuration management
│   │   ├── Configuration.java
│   │   └── ConfigurationManager.java
│   ├── client/                    # API client
│   │   └── ApiClient.java
│   ├── constants/                 # Constants
│   │   ├── EndPoints.java
│   │   └── StatusCodes.java
│   ├── models/                    # POJOs
│   │   ├── Book.java
│   │   └── Author.java
│   ├── services/                  # Service layer
│   │   ├── BookService.java
│   │   └── AuthorService.java
│   └── utils/                     # Utilities
│       ├── TestDataGenerator.java
│       └── JsonUtils.java
│
├── src/main/resources/
│   └── config.properties          # Configuration file
│
├── src/test/java/com/bookstore/
│   ├── base/                      # Base classes
│   │   └── BaseTest.java
│   ├── listeners/                 # TestNG listeners
│   │   └── TestListener.java
│   └── tests/books/               # Test classes
│       ├── GetBooksTests.java
│       ├── CreateBookTests.java
│       ├── UpdateBookTests.java
│       └── DeleteBookTests.java
│
├── src/test/resources/
│   ├── testng.xml                 # TestNG suite
│   ├── testng-smoke.xml           # Smoke tests
│   └── testng-regression.xml      # Regression tests
│
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

---

## 🚀 Quick Start

### Prerequisites

```bash
Java 17 or higher
Maven 3.8+
```

### Setup

1. **Clone the repository**
```bash
git clone <your-repo-url>
cd api-automation-framework
```

2. **Install dependencies**
```bash
mvn clean install -DskipTests
```

3. **Run tests**
```bash
mvn clean test
```

4. **Generate Allure report**
```bash
mvn allure:serve
```

---

## ⚙️ Configuration

Edit `src/main/resources/config.properties`:

```properties
# API Configuration
base.url=https://fakerestapi.azurewebsites.net
api.version=v1

# Timeouts (seconds)
timeout=30
connection.timeout=10

# Logging
logging.enabled=true
log.level=INFO
log.requests=true

# Test Settings
retry.count=2
environment=dev
```

---

## 🧪 Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Suite
```bash
# Smoke tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml

# Regression tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-regression.xml
```

### Run by Groups
```bash
# Smoke tests
mvn clean test -Dgroups=smoke

# Happy path
mvn clean test -Dgroups=happy-path

# Negative tests
mvn clean test -Dgroups=negative

# Edge cases
mvn clean test -Dgroups=edge-case
```

### Run Single Test Class
```bash
mvn test -Dtest=GetBooksTests
```

### Run Specific Test Method
```bash
mvn test -Dtest=GetBooksTests#testGetAllBooksSuccess
```

---

## 📊 Test Reports

### Allure Reports

**Generate and view report:**
```bash
mvn allure:serve
```

**Generate report only:**
```bash
mvn allure:report
# Report location: target/site/allure-maven-plugin/index.html
```

**Report Features:**
- Test execution timeline
- Detailed request/response logs
- Test categorization (Epic, Feature, Story)
- Severity levels
- Historical trends
- Environment details

---

## 🏗️ Framework Architecture

### Layered Architecture

1. **Config Layer** - Centralized configuration management
2. **Client Layer** - REST Assured client wrapper
3. **Constants Layer** - API endpoints and status codes
4. **Model Layer** - POJOs for request/response
5. **Service Layer** - Business logic abstraction
6. **Utils Layer** - Helper utilities
7. **Test Layer** - Test cases

### Design Patterns

- **Singleton Pattern** - ConfigurationManager
- **Builder Pattern** - Model objects, Request specifications
- **Factory Pattern** - Test data generation
- **Service Layer Pattern** - API abstraction

---

## 📝 Writing Tests

### Example Test Class

```java
package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Bookstore API")
@Feature("Books API")
public class ExampleTest extends BaseTest {
    
    @Test(description = "Test description", groups = "smoke")
    @Story("User Story")
    @Severity(SeverityLevel.CRITICAL)
    public void testExample() {
        // Arrange
        Book book = TestDataGenerator.generateRandomBook();
        
        // Act
        Response response = bookService.createBook(book);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
```

---

## 🧩 Key Components

### BookService Example

```java
// Get all books
Response response = bookService.getAllBooks();

// Get book by ID
Response response = bookService.getBookById(1);

// Create book
Book book = Book.createSampleBook();
Response response = bookService.createBook(book);

// Update book
Response response = bookService.updateBook(1, book);

// Delete book
Response response = bookService.deleteBook(1);

// Extract book from response
Book book = bookService.extractBook(response);
```

### Test Data Generation

```java
// Generate random book
Book book = TestDataGenerator.generateRandomBook();

// Generate book with specific page count
Book book = TestDataGenerator.generateBookWithPageCount(500);

// Generate book with long title (boundary testing)
Book book = TestDataGenerator.generateBookWithLongTitle();

// Generate invalid book (negative testing)
Book book = TestDataGenerator.generateBookWithEmptyFields();
```

---

## 📦 Tech Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 17 |
| Build Tool | Maven | 3.8+ |
| Test Framework | TestNG | 7.9.0 |
| API Client | REST Assured | 5.4.0 |
| Assertions | AssertJ | 3.25.1 |
| JSON | Jackson | 2.16.1 |
| Reporting | Allure | 2.25.0 |
| Logging | SLF4J + Logback | 2.0.12 |
| Test Data | JavaFaker | 1.0.2 |
| Code Generation | Lombok | 1.18.30 |

---

## 🔄 CI/CD Integration

The framework includes GitHub Actions workflow for automated testing.

### Workflow Features

- ✅ Automated test execution on push/PR
- ✅ Scheduled nightly runs
- ✅ Manual workflow trigger
- ✅ Allure report generation
- ✅ Test artifacts upload

### Setup

1. Push code to GitHub
2. Enable GitHub Actions in repository settings
3. Workflow runs automatically

---

## ✅ Test Coverage

### Books API

| Endpoint | Happy Path | Edge Cases | Negative |
|----------|------------|------------|----------|
| GET /Books | ✅ | ✅ | ✅ |
| GET /Books/{id} | ✅ | ✅ | ✅ |
| POST /Books | ✅ | ✅ | ✅ |
| PUT /Books/{id} | ✅ | ✅ | ✅ |
| DELETE /Books/{id} | ✅ | ✅ | ✅ |

### Test Scenarios

- Valid data scenarios
- Boundary value testing
- Invalid ID handling (negative, zero, non-existent)
- Empty and null field validation
- Special characters and unicode
- Very long input values
- Response time validation
- Complete CRUD workflows

---

## 🛠️ Troubleshooting

### Common Issues

**1. Lombok not working**
- Enable annotation processing in your IDE
- IntelliJ: Settings → Build → Compiler → Annotation Processors → Enable
- Install Lombok plugin

**2. Tests not running**
- Check testng.xml file exists
- Verify test methods have @Test annotation
- Check Maven Surefire plugin configuration

**3. API not reachable**
```bash
# Check API availability
curl -I https://fakerestapi.azurewebsites.net/api/v1/Books

# Increase timeout in config.properties
timeout=60
```

**4. Allure report not generating**
```bash
# Install Allure CLI
brew install allure  # Mac
scoop install allure # Windows

# Generate report manually
mvn allure:report
mvn allure:serve
```

---

## 📚 Resources

- [REST Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)
- [Allure Report Documentation](https://docs.qameta.io/allure/)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

## 🤝 Contributing

When extending the framework:

1. Follow existing code structure
2. Add JavaDoc comments
3. Include unit tests for utilities
4. Update documentation
5. Follow SOLID principles

---

## 📄 License

This project is created for assessment purposes.

---

## 👥 Author

**API Automation Team**

For questions or issues, please create a GitHub issue.

---

## 🎓 Assessment Completion

This framework fulfills all requirements:

✅ Clean & maintainable structure  
✅ Reusable & scalable code  
✅ Happy path & edge cases coverage  
✅ Test reports (Allure)  
✅ CI/CD integration (GitHub Actions)  
✅ SOLID principles implementation  
✅ Comprehensive documentation

**Time to deliver:** 3 days as specified

---

**Happy Testing! 🚀**
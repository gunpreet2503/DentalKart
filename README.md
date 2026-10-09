# Dentalkart Search Automation Tests

Automated testing suite for the Dentalkart search functionality using Selenium + Java + TestNG. Replaces 5x daily 15-minute manual tests with 2-minute automated execution.

## What's Tested

✅ **Search for "implant"**: Returns 5+ results with name, price, image  
✅ **Search for "scaler"**: Returns 5+ results with name, price, image  
✅ **Page Load Time**: Search results load in <3 seconds  
✅ **Data Integrity**: No empty/missing fields in results

## Quick Start

### Prerequisites
- Java 11+
- Maven 3.6+
- Chrome/Chromium browser (WebDriver auto-downloaded)

### Installation & Running Tests

```bash
# Install dependencies once
mvn clean install

# Run all tests
mvn test

# Run specific test
mvn test -Dtest=DentalkartSearchTest#testImplantSearch

# Generate HTML report
mvn surefire-report:report
open target/site/surefire-report.html
```

### Using the Helper Script

```bash
./run-tests.sh all          # Run all tests
./run-tests.sh implant      # Implant search only
./run-tests.sh scaler       # Scaler search only
./run-tests.sh performance  # Load time test only
./run-tests.sh report       # Generate report
```

## Project Structure

```
src/test/java/com/dentalkart/
├── DentalkartSearchTest.java     ← Main test suite (4 tests)
├── SearchPage.java               ← Page Object Model (element locators)
└── TestDataManager.java          ← DB setup (future use)

src/test/resources/
├── testng.xml                    ← Test configuration
└── config.properties             ← Settings (customizable)
```

## Test Cases

| Test | Validates |
|------|-----------|
| `testImplantSearch` | "implant" keyword returns 5+ products |
| `testScalerSearch` | "scaler" keyword returns 5+ products |
| `testPageLoadTime` | Search page loads in <3 seconds |
| `testNoEmptyResults` | All results have name, price, image |

## How Selectors Work

The selectors in `SearchPage.java` use **flexible XPath patterns** that adapt to React/Next.js apps:

```java
// Search input - tries multiple patterns
//input[@placeholder[contains(., 'search')]] 
  OR //input[@type='search'] 
  OR //input[@name='search']

// Product items - finds any product container
//div[contains(@class, 'product')] 
  OR //article[contains(@class, 'product')] 
  OR //div[@data-testid='product']

// Product details - finds name/price/image within each item
.//h2 OR .//h3 OR .//span[contains(@class, 'name')]
.//span[contains(@class, 'price')] OR .//p[contains(text(), '₹')]
.//img[@alt] OR .//img[@src]
```

**These work out-of-the-box for most modern e-commerce sites.** If they don't match your actual HTML, update them:

### If Tests Fail - Update Selectors

1. Open dentalkart.com in Chrome
2. Right-click → **Inspect** on the search input
3. Note the `id`, `name`, or `placeholder` attribute
4. Update `SearchPage.java` line 15:
   ```java
   private static final By SEARCH_INPUT = By.id("your-actual-id");
   ```

5. Repeat for product items and details
6. Run tests again: `mvn test`

## Configuration

Edit `src/test/resources/config.properties`:
```properties
app.url=https://www.dentalkart.com
search.min.results=5
search.max.load.time=3
```

Or edit `DentalkartSearchTest.java` constants:
```java
private static final String BASE_URL = "https://www.dentalkart.com";
private static final int MIN_RESULTS = 5;
private static final int MAX_PAGE_LOAD_TIME = 3;
```

## CI/CD Integration

### GitHub Actions
```yaml
- name: Run automation tests
  run: mvn test

- name: Upload test report
  if: always()
  uses: actions/upload-artifact@v2
  with:
    name: test-report
    path: target/site/surefire-report.html
```

### Headless Mode (for CI)
Update `DentalkartSearchTest.java` setUp():
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless=new");
options.addArguments("--no-sandbox");
driver = new ChromeDriver(options);
```

### Run Daily via Cron
```bash
# In your CI pipeline:
0 2 * * * cd /path/to/automation && mvn test
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `TimeoutException` | Increase wait time in `SearchPage.java` (line 47) |
| `NoSuchElementException` | Update XPath selectors - inspect page with browser DevTools |
| `Connection refused` | Check https://www.dentalkart.com is accessible |
| Tests run locally but fail in CI | Add `--no-sandbox` to ChromeOptions |
| Slow performance | Check network - increase `MAX_PAGE_LOAD_TIME` if >3s is expected |

## What Happens When Search Is Broken

Before automation (manual):
- Run test 5 times/day × 15 min = **75 minutes wasted**
- High chance of human error
- Regressions slip to production

After automation:
- Runs automatically on every push
- Catches broken search **immediately**
- Full test report in 2 minutes
- Zero manual overhead

## Next Steps

1. ✅ Run `mvn test` to verify setup works
2. ✅ Update selectors if elements don't match
3. ✅ Add to CI/CD pipeline (GitHub Actions, Jenkins, etc.)
4. ✅ Set up daily schedule for automated runs
5. Optional: Add more keywords, performance thresholds, screenshot capture on failures

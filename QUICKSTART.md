# Quick Start - 5 Minute Setup

## 1. Install & Run (one command)
```bash
mvn clean test
```

That's it. The tests will:
- Install Selenium, ChromeDriver, etc. automatically
- Launch dentalkart.com
- Search for "implant" → verify 5+ results
- Search for "scaler" → verify 5+ results
- Check page loads in <3 seconds
- Print a report to your terminal

## 2. If Tests Fail - Fix the Selectors (2 minutes)

**What it means**: The HTML structure doesn't match our selectors.

**How to fix**:

1. Open https://www.dentalkart.com in Chrome
2. Click the search box, right-click → **Inspect**
3. You'll see something like:
   ```html
   <input id="searchBox" placeholder="Search products...">
   ```
4. Open `src/test/java/com/dentalkart/pages/SearchPage.java`
5. Change line 15 from:
   ```java
   private static final By SEARCH_INPUT = By.xpath("//input[@placeholder[contains(., 'search')]]...");
   ```
   To:
   ```java
   private static final By SEARCH_INPUT = By.id("searchBox");
   ```
6. Run `mvn test` again

**Need more help?** See the "How Selectors Work" section in README.md

## 3. Use the Helper Script
```bash
./run-tests.sh all         # Run all tests
./run-tests.sh implant     # Just implant search
./run-tests.sh performance # Just load time test
./run-tests.sh report      # Generate HTML report
```

## 4. Add to Your CI/CD

### GitHub Actions
```yaml
name: Search Automation Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
      - run: mvn test
      - uses: actions/upload-artifact@v3
        if: always()
        with:
          name: test-report
          path: target/site/surefire-report.html
```

### Daily Schedule (Cron)
```bash
# Run every day at 2am
0 2 * * * cd /path/to/automation && mvn clean test
```

## 5. View Test Report
```bash
# After tests run:
mvn surefire-report:report
open target/site/surefire-report.html
```

---

**That's all!** Your search tests are now automated. No more manual testing.

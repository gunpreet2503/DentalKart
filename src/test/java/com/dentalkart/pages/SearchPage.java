package com.dentalkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SearchPage {
  private WebDriver driver;
  private WebDriverWait wait;

  // Dentalkart.com selectors
  private static final By SEARCH_INPUT = By.xpath("//input[@type='text' or @placeholder]");
  private static final By SEARCH_BUTTON = By.xpath("//button");

  // Product cards - look for product containers in search results
  private static final By PRODUCT_ITEMS = By.xpath("//a[contains(@href, '/p/')] | //div[contains(@class, 'ProductCard')] | //div[contains(@class, 'product')]//a[contains(@href, '/p/')]/../..");

  // Product details - within product card
  private static final By PRODUCT_NAME = By.xpath(".//a[contains(@href, '/p/')] | .//h2 | .//span[@title]");
  private static final By PRODUCT_PRICE = By.xpath(".//span[contains(., '₹')] | .//strong[contains(., '₹')]");
  private static final By PRODUCT_IMAGE = By.xpath(".//img[@src and contains(@src, 'cloudinary')]");

  public SearchPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void navigateTo(String url) throws InterruptedException {
    driver.get(url);
    Thread.sleep(3000);
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("window.scrollTo(0, 0);");
    Thread.sleep(500);
  }

  public void searchFor(String keyword) throws InterruptedException {
    try {
      WebElement searchInput = findSearchInput();
      if (searchInput == null) {
        throw new RuntimeException("Could not find search input on the page");
      }

      // Wait for input to be ready
      wait.until(ExpectedConditions.elementToBeClickable(searchInput));
      Thread.sleep(500);

      // Click to focus, then clear and type
      searchInput.click();
      Thread.sleep(300);

      // Use keyboard to clear instead of clear()
      searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
      Thread.sleep(200);
      searchInput.sendKeys(keyword);
      Thread.sleep(500);

      // Press Enter to search
      searchInput.sendKeys(Keys.RETURN);
      Thread.sleep(2000);

      wait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(PRODUCT_ITEMS)
      );
    } catch (Exception e) {
      System.err.println("Search failed: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("Failed to perform search for: " + keyword, e);
    }
  }

  private WebElement findSearchInput() throws InterruptedException {
    By[] strategies = {
        By.xpath("//input[contains(@placeholder, 'search') or contains(@placeholder, 'Search')]"),
        By.xpath("//input[@name='q']"),
        By.xpath("//input[@type='search']"),
        By.xpath("//input[contains(@class, 'search')]"),
        By.xpath("//input[@placeholder]"),
        By.xpath("//input[@type='text' and (@placeholder or @aria-label)]")
    };

    for (By strategy : strategies) {
      try {
        List<WebElement> elements = driver.findElements(strategy);
        for (WebElement element : elements) {
          if (isInteractable(element)) {
            return element;
          }
        }
      } catch (Exception ignored) {
        // Try next strategy
      }
    }
    return null;
  }

  private boolean isInteractable(WebElement element) {
    try {
      return element.isDisplayed() && element.isEnabled();
    } catch (Exception e) {
      return false;
    }
  }

  public List<WebElement> getSearchResults() {
    return driver.findElements(PRODUCT_ITEMS);
  }

  public int getResultCount() {
    return getSearchResults().size();
  }

  public String getProductName(WebElement product) {
    try {
      WebElement nameElement = product.findElement(PRODUCT_NAME);
      String text = nameElement.getText();
      if (text.trim().isEmpty()) {
        text = nameElement.getAttribute("title");
      }
      if (text.trim().isEmpty()) {
        text = nameElement.getAttribute("aria-label");
      }
      return text.trim();
    } catch (Exception e) {
      return product.getText().split("\n")[0].trim();
    }
  }

  public String getProductPrice(WebElement product) {
    try {
      return product.findElement(PRODUCT_PRICE).getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getProductImageSrc(WebElement product) {
    try {
      return product.findElement(PRODUCT_IMAGE).getAttribute("src");
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isSearchResultsDisplayed() {
    try {
      return wait.until(ExpectedConditions.presenceOfElementLocated(PRODUCT_ITEMS)) != null;
    } catch (Exception e) {
      return false;
    }
  }

  public long getPageLoadTime(String keyword) throws InterruptedException {
    long startTime = System.currentTimeMillis();
    searchFor(keyword);
    long endTime = System.currentTimeMillis();
    return (endTime - startTime) / 1000;
  }

  public void clickOnProduct(int productIndex) throws InterruptedException {
    try {
      List<WebElement> products = getSearchResults();
      if (productIndex < 0 || productIndex >= products.size()) {
        throw new IndexOutOfBoundsException("Product index " + productIndex + " out of range. Total products: " + products.size());
      }

      WebElement product = products.get(productIndex);
      wait.until(ExpectedConditions.elementToBeClickable(product));
      product.click();
      Thread.sleep(2000);
    } catch (Exception e) {
      System.err.println("Failed to click on product: " + e.getMessage());
      throw new RuntimeException("Failed to click on product at index: " + productIndex, e);
    }
  }

  public void clickOnProductByName(String productName) throws InterruptedException {
    try {
      List<WebElement> products = getSearchResults();
      boolean found = false;

      for (WebElement product : products) {
        String name = product.getText().toLowerCase();
        if (name.contains(productName.toLowerCase())) {
          wait.until(ExpectedConditions.elementToBeClickable(product));
          product.click();
          Thread.sleep(2000);
          found = true;
          break;
        }
      }

      if (!found) {
        throw new RuntimeException("Product with name '" + productName + "' not found in search results");
      }
    } catch (Exception e) {
      System.err.println("Failed to click on product: " + e.getMessage());
      throw new RuntimeException("Failed to click on product with name: " + productName, e);
    }
  }

  public void addProductToCart(int quantity) throws InterruptedException {
    try {
      Thread.sleep(1000);
      JavascriptExecutor js = (JavascriptExecutor) driver;

      // Look for Add to Cart button with various selectors
      By[] addToCartSelectors = {
          By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add to cart')]"),
          By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add to cart')]"),
          By.xpath("//button[@aria-label and contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cart')]"),
          By.xpath("//button[contains(translate(@class, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cart')]"),
          By.xpath("//button[contains(translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cart')]"),
          By.xpath("//div[contains(@class, 'add-to-cart')]//button"),
          By.xpath("//input[@value and contains(translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cart')]"),
          By.xpath("//form//button[1]"),
          By.xpath("//button[@type='submit'][1]"),
          By.xpath("//button[contains(., 'ADD')]"),
          By.xpath("//button[position()=1]")
      };

      WebElement addToCartButton = null;
      for (By selector : addToCartSelectors) {
        try {
          List<WebElement> elements = driver.findElements(selector);
          if (!elements.isEmpty()) {
            for (WebElement candidate : elements) {
              if (isInteractable(candidate)) {
                addToCartButton = candidate;
                System.out.println("Found add to cart button: " + candidate.getText());
                break;
              }
            }
            if (addToCartButton != null) break;
          }
        } catch (Exception ignored) {
          // Try next selector
        }
      }

      if (addToCartButton == null) {
        String pageSource = driver.getPageSource();
        if (pageSource.toLowerCase().contains("add to cart")) {
          System.out.println("Page contains 'add to cart' text but button not found");
        }
        throw new RuntimeException("Could not find 'Add to Cart' button on the page");
      }

      wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
      Thread.sleep(500);
      js.executeScript("arguments[0].scrollIntoView(true);", addToCartButton);
      Thread.sleep(300);

      try {
        addToCartButton.click();
      } catch (Exception e) {
        System.out.println("Standard click failed, trying JavaScript click");
        js.executeScript("arguments[0].click();", addToCartButton);
      }
      Thread.sleep(2000);
    } catch (Exception e) {
      System.err.println("Failed to add product to cart: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("Failed to add product to cart", e);
    }
  }

  public void addFirstProductToCart() throws InterruptedException {
    try {
      clickOnProduct(0);
      addProductToCart(1);
    } catch (Exception e) {
      System.err.println("Failed to add first product to cart: " + e.getMessage());
      throw new RuntimeException("Failed to add first product to cart", e);
    }
  }
}

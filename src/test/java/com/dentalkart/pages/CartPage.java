package com.dentalkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
  private WebDriver driver;
  private WebDriverWait wait;

  // Cart selectors
  private static final By CART_ICON = By.xpath("//a[contains(@href, '/cart')] | //button[contains(@aria-label, 'cart')]");
  private static final By CART_ITEMS = By.xpath("//tr[contains(@class, 'cart-item')] | //div[contains(@class, 'cart-item')] | //div[contains(@class, 'product-row')]");
  private static final By PRODUCT_NAME_IN_CART = By.xpath(".//td[@class='product-name'] | .//a[contains(@href, '/p/')] | .//div[contains(@class, 'product-name')]");
  private static final By PRODUCT_PRICE_IN_CART = By.xpath(".//td[@class='product-price'] | .//span[contains(., '₹')]");
  private static final By PRODUCT_QUANTITY = By.xpath(".//input[@type='number'] | .//span[contains(@class, 'quantity')]");
  private static final By EMPTY_CART_MESSAGE = By.xpath("//h2[contains(., 'Cart is empty')] | //p[contains(., 'Your cart is empty')]");
  private static final By CHECKOUT_BUTTON = By.xpath("//button[contains(., 'Checkout')] | //a[contains(., 'Checkout')]");

  public CartPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void navigateToCart() throws InterruptedException {
    try {
      WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(CART_ICON));
      cartIcon.click();
      Thread.sleep(2000);
    } catch (Exception e) {
      driver.get("https://www.dentalkart.com/cart");
      Thread.sleep(2000);
    }
  }

  public List<WebElement> getCartItems() {
    return driver.findElements(CART_ITEMS);
  }

  public int getCartItemCount() {
    return getCartItems().size();
  }

  public boolean isProductInCart(String productName) throws InterruptedException {
    navigateToCart();
    List<WebElement> cartItems = getCartItems();

    for (WebElement item : cartItems) {
      try {
        WebElement nameElement = item.findElement(PRODUCT_NAME_IN_CART);
        String itemName = nameElement.getText().toLowerCase();
        if (itemName.contains(productName.toLowerCase())) {
          return true;
        }
      } catch (Exception e) {
        // Continue to next item
      }
    }
    return false;
  }

  public boolean isCartEmpty() {
    try {
      return !driver.findElements(EMPTY_CART_MESSAGE).isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public String getProductPriceInCart(String productName) throws InterruptedException {
    navigateToCart();
    List<WebElement> cartItems = getCartItems();

    for (WebElement item : cartItems) {
      try {
        WebElement nameElement = item.findElement(PRODUCT_NAME_IN_CART);
        String itemName = nameElement.getText().toLowerCase();
        if (itemName.contains(productName.toLowerCase())) {
          WebElement priceElement = item.findElement(PRODUCT_PRICE_IN_CART);
          return priceElement.getText();
        }
      } catch (Exception e) {
        // Continue to next item
      }
    }
    return "";
  }

  public String getProductQuantityInCart(String productName) throws InterruptedException {
    navigateToCart();
    List<WebElement> cartItems = getCartItems();

    for (WebElement item : cartItems) {
      try {
        WebElement nameElement = item.findElement(PRODUCT_NAME_IN_CART);
        String itemName = nameElement.getText().toLowerCase();
        if (itemName.contains(productName.toLowerCase())) {
          WebElement quantityElement = item.findElement(PRODUCT_QUANTITY);
          return quantityElement.getAttribute("value");
        }
      } catch (Exception e) {
        // Continue to next item
      }
    }
    return "";
  }

  public void clearCart() throws InterruptedException {
    navigateToCart();
    List<WebElement> removeButtons = driver.findElements(By.xpath("//button[contains(., 'Remove')] | //a[contains(., 'Remove')]"));

    while (!removeButtons.isEmpty()) {
      removeButtons.get(0).click();
      Thread.sleep(1000);
      removeButtons = driver.findElements(By.xpath("//button[contains(., 'Remove')] | //a[contains(., 'Remove')]"));
    }
  }

  /**
   * Remove a specific product from cart by product name
   *
   * Example: removeProductByName("Titanium Implant")
   *
   * @param productName - Name of product to remove
   * @throws InterruptedException if thread is interrupted
   */
  public void removeProductByName(String productName) throws InterruptedException {
    navigateToCart();
    List<WebElement> cartItems = getCartItems();

    for (WebElement item : cartItems) {
      try {
        WebElement nameElement = item.findElement(PRODUCT_NAME_IN_CART);
        String itemName = nameElement.getText().toLowerCase();

        if (itemName.contains(productName.toLowerCase())) {
          // Found the product, now find and click the Remove button for this item
          List<WebElement> removeButtons = item.findElements(By.xpath(".//button[contains(., 'Remove')] | .//a[contains(., 'Remove')]"));

          if (!removeButtons.isEmpty()) {
            removeButtons.get(0).click();
            Thread.sleep(1000);
            return;
          } else {
            throw new RuntimeException("Remove button not found for product: " + productName);
          }
        }
      } catch (Exception e) {
        // Continue to next item
      }
    }

    throw new RuntimeException("Product not found in cart: " + productName);
  }

  /**
   * Remove a product from cart by its index position
   *
   * Example: removeProductByIndex(0) - removes first item
   * Example: removeProductByIndex(1) - removes second item
   *
   * @param index - Position of product (0-based)
   * @throws InterruptedException if thread is interrupted
   */
  public void removeProductByIndex(int index) throws InterruptedException {
    navigateToCart();
    List<WebElement> cartItems = getCartItems();

    if (index < 0 || index >= cartItems.size()) {
      throw new IndexOutOfBoundsException(
          "Product index " + index + " out of range. Total products in cart: " + cartItems.size()
      );
    }

    WebElement item = cartItems.get(index);

    try {
      List<WebElement> removeButtons = item.findElements(By.xpath(".//button[contains(., 'Remove')] | .//a[contains(., 'Remove')]"));

      if (!removeButtons.isEmpty()) {
        removeButtons.get(0).click();
        Thread.sleep(1000);
      } else {
        throw new RuntimeException("Remove button not found for item at index: " + index);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to remove product at index " + index + ": " + e.getMessage(), e);
    }
  }

  /**
   * Get product name at specific index in cart
   *
   * @param index - Position of product (0-based)
   * @return Product name or empty string if not found
   */
  public String getProductNameByIndex(int index) throws InterruptedException {
    navigateToCart();
    List<WebElement> cartItems = getCartItems();

    if (index < 0 || index >= cartItems.size()) {
      return "";
    }

    try {
      WebElement item = cartItems.get(index);
      WebElement nameElement = item.findElement(PRODUCT_NAME_IN_CART);
      return nameElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Verify product exists at specific index in cart
   *
   * @param index - Position to check (0-based)
   * @param expectedName - Expected product name
   * @return true if product name matches at that index
   */
  public boolean verifyProductAtIndex(int index, String expectedName) throws InterruptedException {
    String actualName = getProductNameByIndex(index);
    return actualName.toLowerCase().contains(expectedName.toLowerCase());
  }
}

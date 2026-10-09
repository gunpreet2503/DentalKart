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
}

package nl.avans;


import nl.avans.data.Product;
import org.openqa.selenium.By;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import nl.avans.context.TestContext;
import nl.avans.data.TestDataProvider;
import nl.avans.data.User;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class DemoWebshopTests {
	private final String PATH_LOGIN = "/login";

	private final String WEBSHOP_URL = "https://demowebshop.tricentis.com";
	private final String WEBSHOP_URL_LOGIN = WEBSHOP_URL + PATH_LOGIN;

	private final String WEBSHOP_TITLE = "Demo Web Shop";
	private final String WEBSHOP_TITLE_LOGIN = WEBSHOP_TITLE + ". Login";

	private final String SELECTOR_HEADER_LINKS = ".header-links";

	private final String SELECTOR_LOGIN_LINK = SELECTOR_HEADER_LINKS + " [href=\"" + PATH_LOGIN + "\"]";
	private final String SELECTOR_ACCOUNT_LINK = SELECTOR_HEADER_LINKS + " [href=\"/customer/info\"]";
	private final String SELECTOR_LOGOUT_LINK = SELECTOR_HEADER_LINKS + " [href=\"/logout\"]";
	private final String SELECTOR_HEADER_MENU = ".header-menu";
	private final String SELECTOR_ELECTRONICS_LINK = "a[href=\"/electronics\"]";

	private final String SELECTOR_SMARTPHONE_IMAGE_LINK = "div.sub-category-grid div.item-box:nth-child(2) a[href=\"/cell-phones\"]";
	;
	private final String SELECTOR_ADD_TO_CART_BUTTON = "button-1 add-to-cart-button";

	private final String SELECTOR_LOGIN_FORM = "form[action=\"" + PATH_LOGIN + "\"]";
	private final String SELECTOR_LOGIN_FORM_EMAIL = SELECTOR_LOGIN_FORM + " #Email";
	private final String SELECTOR_LOGIN_FORM_PASSWORD = SELECTOR_LOGIN_FORM + " #Password";
	private final String SELECTOR_LOGIN_FORM_SUBMIT = SELECTOR_LOGIN_FORM + " input[type=\"submit\"]";

	static final String ADD_TO_SHOPPING_CART_OK = "The product has been added to your shopping cart";
	static final String ADD_TO_SHOPPING_CART_NOK = "Quantity should be positive";

	@Rule
	public TestContext context = TestContext.Create();

	@Test
	public void test0_Home() throws Exception {
		// Open website
		open();
		// Test home page
		homepage();
	}

	@Test
	public void test1_Login() throws Exception {

		// Read valid users from datasource
		List<User> users = TestDataProvider.instance.readTestData("valid_users.csv", User.class);
		Assert.assertNotEquals(0, users.size());

		// Open website
		open();

		// Test homepage
		homepage();

		// Test every valid user with login
		for (User user : users) {
			// Login user
			login(user);
			// Logout user
			logout();
		}
	}

	@Test
	public void test1_AddToCart() throws Exception {
		// Read valid users from datasource
		List<User> users = TestDataProvider.instance.readTestData("valid_users.csv", User.class);
		Assert.assertNotEquals(0, users.size());
		
		// Open website
		open();
		
		// Test homepage
		homepage();
		
		Random random = new Random();
		User randomUser = users.get(random.nextInt(users.size()));
		
		// Test a random login.
		login(randomUser);

		// Go to the Cell phones category
		context.driver().findElement(By.cssSelector(SELECTOR_HEADER_MENU)).click();
		context.driver().findElement(By.cssSelector(SELECTOR_ELECTRONICS_LINK)).click();
		WebDriverWait wait = new WebDriverWait(context.driver(), Duration.ofSeconds(10));
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(SELECTOR_SMARTPHONE_IMAGE_LINK))).click();

		// Select the Smartphone category
		//TODO set random between smartphone/used phone
		wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Smartphone"))).click();

		// Set the warranty quantity to 5
		//TODO set random numbers between 2 and 12.
		WebElement quantityInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("addtocart_43.EnteredQuantity")));
		quantityInput.clear();
		quantityInput.sendKeys("5");

		// Add smartphones with the specified warranty quantity to the cart
		JavascriptExecutor executor = (JavascriptExecutor) context.driver();
		executor.executeScript("$('#addtocart_43_EnteredQuantity').keydown(function(event) { if (event.keyCode == 13) { $('#add-to-cart-button-43').click(); return false; } });");
		wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-43"))).click();


		// Navigate to Apparel & Shoes category
		context.driver().findElement(By.cssSelector(SELECTOR_HEADER_MENU)).click();
		context.driver().findElement(By.linkText("Apparel & Shoes")).click();

		// Select Blue Jeans
		wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Blue Jeans"))).click();

		// Set the warranty quantity to 5
		//TODO set random numbers between 2 and 12.
		WebElement quantityInput1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("addtocart_36.EnteredQuantity")));
		quantityInput1.clear();
		quantityInput1.sendKeys("5");

		// Add smartphones with the specified warranty quantity to the cart
		JavascriptExecutor executor1 = (JavascriptExecutor) context.driver();
		executor1.executeScript("$('#addtocart_36_EnteredQuantity').keydown(function(event) { if (event.keyCode == 13) { $('#add-to-cart-button-36').click(); return false; } });");
		wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-36"))).click();

		// Go to the shopping cart
		context.driver().findElement(By.cssSelector(SELECTOR_HEADER_MENU)).click();
		context.driver().findElement(By.cssSelector("#topcartlink a.ico-cart")).click();

		// Get the total amount of the shopping cart
		WebElement totalAmountElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cart-total-right .product-price")));
		String totalAmount = totalAmountElement.getText();

		// Select all checkboxes
		List<WebElement> checkboxes = context.driver().findElements(By.name("removefromcart"));
		for (WebElement checkbox : checkboxes) {
			if (!checkbox.isSelected()) {
				checkbox.click();
			}
		}

		// Remove all items from the cart by updating the cart
		context.driver().findElement(By.name("updatecart")).click();
	}

	public void open() throws Exception {
		context.driver().get(WEBSHOP_URL);
	}

	public void homepage() throws Exception {
		String title = context.driver().getTitle();
		Assert.assertEquals(WEBSHOP_TITLE, title);
	}

	public void login(User user) throws Exception {
		open();

		context.driver().findElement(By.cssSelector(SELECTOR_LOGIN_LINK)).click();

		Assert.assertEquals(WEBSHOP_URL_LOGIN, context.driver().getCurrentUrl());
		Assert.assertEquals(WEBSHOP_TITLE_LOGIN, context.driver().getTitle());

		context.driver().findElement(By.cssSelector(SELECTOR_LOGIN_FORM_EMAIL)).sendKeys(user.email);
		context.driver().findElement(By.cssSelector(SELECTOR_LOGIN_FORM_PASSWORD)).sendKeys(user.password);
		context.driver().findElement(By.cssSelector(SELECTOR_LOGIN_FORM_SUBMIT)).click();

		homepage();

		Assert.assertEquals(user.email, context.driver().findElement(By.cssSelector(SELECTOR_ACCOUNT_LINK)).getText());
	}

	public void logout() {
		context.driver().findElement(By.cssSelector(SELECTOR_LOGOUT_LINK)).click();
	}

	@Test
	public void test1_AddToCarts() throws Exception {
		List<User> users = readUsers("valid_users.csv");
		List<Product> products = readProducts("valid_products.csv");
		
		open();
		homepage();
		
		User randomUser = getRandomUser(users);
		
		login(randomUser);
		navigateToCart();
		removeItemsFromCart();

		Random random = new Random(System.currentTimeMillis());
		
		double totalPrice = 0;
		
		while(totalPrice < 5000) {

			int index = random.nextInt(products.size());
			int orderAmount = random.nextInt(5);
			
			Product product = products.get(index);
			int actualOrderAmount = addProductToShoppingCart(product, orderAmount);

			totalPrice += product.getPrice() * actualOrderAmount;
		}

		navigateToCart();
		
		double cartTotalPrice = getCartTotal();
		Assert.assertEquals(totalPrice, cartTotalPrice, 0.1);  // Tolerance is set to 0.01
		
		removeItemsFromCart();
		logout();
	}

	private double getCartTotal() {

		WebDriverWait wait = new WebDriverWait(context.driver(), Duration.ofSeconds(10));

		By totalPriceSelector = By.cssSelector(".product-price.order-total > strong");
		String cartTotalText = wait.until(ExpectedConditions.presenceOfElementLocated(totalPriceSelector)).getText();
	
		return Double.parseDouble(cartTotalText.replace(",", ".")); // Parse the total as a double
	}

	private List<User> readUsers(String filename) throws Exception {
		List<User> users = TestDataProvider.instance.readTestData(filename, User.class);
		Assert.assertNotEquals(0, users.size());
		return users;
	}

	private List<Product> readProducts(String filename) throws Exception {
		List<Product> products = TestDataProvider.instance.readTestData(filename, Product.class);
		Assert.assertNotEquals(0, products.size());
		return products;
	}

	private User getRandomUser(List<User> users) {
		Random random = new Random();
		return users.get(random.nextInt(users.size()));
	}

	/**
	 * @param item the actual item to order
	 * @param quantity order quantity
	 * @return number of items ordered
	 */
	private int addProductToShoppingCart(Product item, int quantity) {

		//Wait for element waiter
		WebDriverWait wait = new WebDriverWait(context.driver(), Duration.ofSeconds(10));
		
		// Find category url link
		By categorySelector = By.cssSelector(String.format(".top-menu [href=\"/%s\"]", item.getCategory()));
		wait.until(ExpectedConditions.elementToBeClickable(categorySelector)).click();

		// If it has a sub category then navigate further
		if(item.getSubCategory().isEmpty() == false) {
			By subcategorySelector = By.cssSelector(String.format(".sub-category-item [href=\"/%s\"]", item.getSubCategory()));
			wait.until(ExpectedConditions.elementToBeClickable(subcategorySelector)).click();
		}
		
		// Find item link and click it
		By itemLink = By.cssSelector(String.format("[data-productid=\"%d\"] .product-title > a", item.getId()));
		wait.until(ExpectedConditions.elementToBeClickable(itemLink)).click();
		
		// Check if the item is out of stock.
		By stockSelector = By.cssSelector(".stock .value");
		
		WebElement availabilityLabel = null;

		try {
			availabilityLabel = context.driver().findElement(stockSelector);
		} catch(NoSuchElementException ex) {

		}

		// If this is null then there is no stock
		if(availabilityLabel != null) 
		{
			boolean isAvailable = availabilityLabel.getText().equalsIgnoreCase("in stock");

			// Compare availability
			Assert.assertEquals(item.isAvailable(), isAvailable);
	
			// Do not add any item to the cart
			if(item.isAvailable() == false) return 0;
		}


		if(item.getCategory().equalsIgnoreCase("gift-cards")){

			By recipientInputSelector = By.cssSelector(String.format("input[name=\"giftcard_%d.RecipientName\"]", item.getId()));
			WebElement recipientInput = wait.until(ExpectedConditions.presenceOfElementLocated(recipientInputSelector));
			
			recipientInput.clear();
			recipientInput.sendKeys("Johnny Bakker");
		}
			
		// Item is in stock, so it's safe to set the quantity.
		By quantityInputSelector = By.cssSelector(String.format("input[name=\"addtocart_%d.EnteredQuantity\"]", item.getId()));
		WebElement quantityInput = wait.until(ExpectedConditions.presenceOfElementLocated(quantityInputSelector));
			
		// Clear quantity input field
		quantityInput.clear();

		// Set quantity
		quantityInput.sendKeys(String.valueOf(quantity));

		By addToCartButtonSelector = By.id(String.format("add-to-cart-button-%d", item.getId()));
		wait.until(ExpectedConditions.elementToBeClickable(addToCartButtonSelector)).click();
			
		By notificationBarTextSelector = By.cssSelector("#bar-notification > p");
		String notificationText = wait.until(ExpectedConditions.visibilityOfElementLocated(notificationBarTextSelector)).getText();
		
		// expected notification text
		String expectedNotificationText = quantity > 0 ? ADD_TO_SHOPPING_CART_OK : ADD_TO_SHOPPING_CART_NOK;
		Assert.assertEquals(expectedNotificationText, notificationText);

		return quantity;
	}

	private void navigateToCart() {
		context.driver().findElement(By.cssSelector(SELECTOR_HEADER_MENU)).click();
		context.driver().findElement(By.cssSelector("#topcartlink a.ico-cart")).click();
	}

	private void removeItemsFromCart() {
		
		WebDriverWait wait = new WebDriverWait(context.driver(), Duration.ofSeconds(10));
		
		By summary = By.cssSelector(".order-summary-content");
		String summaryText = context.driver().findElement(summary).getText();
		
		if (summaryText.equalsIgnoreCase("Your Shopping Cart is empty!")) {
			return;
		}

		List<WebElement> checkboxes = context.driver().findElements(By.name("removefromcart"));
		for (WebElement checkbox : checkboxes) {
			if (!checkbox.isSelected()) {
				checkbox.click();
			}
		}

		context.driver().findElement(By.name("updatecart")).click();
		//String removedFromCartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(BAR_NOTIFICATION))).getText();
		//Assert.assertTrue(removedFromCartMessage.contains("Your Shopping Cart is empty!"));
	}
}



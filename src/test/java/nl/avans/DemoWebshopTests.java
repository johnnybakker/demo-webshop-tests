package nl.avans;


import org.openqa.selenium.By;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import nl.avans.context.TestContext;
import nl.avans.data.CsvTestDataProvider;
import nl.avans.data.models.Product;
import nl.avans.data.models.User;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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

	private final String SELECTOR_LOGIN_FORM = "form[action=\"" + PATH_LOGIN + "\"]";
	private final String SELECTOR_LOGIN_FORM_EMAIL = SELECTOR_LOGIN_FORM + " #Email";
	private final String SELECTOR_LOGIN_FORM_PASSWORD = SELECTOR_LOGIN_FORM + " #Password";
	private final String SELECTOR_LOGIN_FORM_SUBMIT = SELECTOR_LOGIN_FORM + " input[type=\"submit\"]";

	static final String ADD_TO_SHOPPING_CART_OK = "The product has been added to your shopping cart";
	static final String ADD_TO_SHOPPING_CART_NOK = "Quantity should be positive";

	@Rule
	public TestContext context = TestContext.Create(new CsvTestDataProvider());

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
		List<User> users = context.dataProvider().read("users/users", User.class);
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
	public void test2_CaseCheckTotalPrice() throws Exception {
		
		User user = context.dataProvider().read("users/users", User.class).get(0);
		List<Product> products_1 = context.dataProvider().read("products/case_products_1", Product.class);
		List<Product> products_2 = context.dataProvider().read("products/case_products_2", Product.class);
		
		open();
		homepage();
		
		login(user);
		navigateToCart();
		removeItemsFromCart();
	
		final int MIN_ORDER_AMOUNT = 3, MAX_ORDER_AMOUNT = 9;
		int product_1_index = getRandomInt(0, products_1.size() - 1);
		int product_2_index = getRandomInt(0, products_2.size() - 1);	
		int product_1_amount = getRandomInt(MIN_ORDER_AMOUNT, MAX_ORDER_AMOUNT);
		int product_2_amount = getRandomInt(MIN_ORDER_AMOUNT, MAX_ORDER_AMOUNT);
		
		Product product_1 = products_1 .get(product_1_index);
		Product product_2 = products_2 .get(product_2_index);
		
		int product_1_actual_order_amount = addProductToShoppingCart(product_1, product_1_amount);
		int product_2_actual_order_amount = addProductToShoppingCart(product_2, product_2_amount);
		
		double totalPrice = 0;
		totalPrice += product_1.getPrice() * product_1_actual_order_amount;
		totalPrice += product_2.getPrice() * product_2_actual_order_amount;

		navigateToCart();
		
		double cartTotalPrice = getCartTotal();
		Assert.assertEquals(totalPrice, cartTotalPrice, 0.1);  // Tolerance is set to 0.01
		
		removeItemsFromCart();
		logout();
	}

	@Test
	public void test2_FillShoppingCartAndCheckTotal() throws Exception {
		List<User> users = context.dataProvider().read("users/users", User.class);
		List<Product> products = context.dataProvider().read("products/valid_products", Product.class);
		
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

	private double getCartTotal() {

		WebDriverWait wait = new WebDriverWait(context.driver(), Duration.ofSeconds(10));

		By totalPriceSelector = By.cssSelector(".cart-total .product-price");
		String cartTotalText = wait.until(ExpectedConditions.presenceOfElementLocated(totalPriceSelector)).getText();
	
		return Double.parseDouble(cartTotalText.replace(",", ".")); // Parse the total as a double
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
		
		Actions actions = new Actions(context.driver());

		// Find category url link
		By categorySelector = By.cssSelector(String.format(".top-menu [href=\"/%s\"]", item.getCategory()));
		WebElement category = context.driver().findElement(categorySelector);
		actions.moveToElement(category);
		wait.until(ExpectedConditions.elementToBeClickable(category)).click();

		// If it has a sub category then navigate further
		if(item.getSubCategory().isEmpty() == false) {
			By subcategorySelector = By.cssSelector(String.format(".sub-category-item [href=\"/%s\"]", item.getSubCategory()));
			WebElement subcategory = context.driver().findElement(subcategorySelector);
			actions.moveToElement(subcategory);
			wait.until(ExpectedConditions.elementToBeClickable(subcategory)).click();
		}
		
		// Find item link and click it
		By itemLink = By.cssSelector(String.format("[data-productid=\"%d\"] .product-title > a", item.getId()));
		wait.until(ExpectedConditions.elementToBeClickable(itemLink)).click();
		
		// Check if the item is out of stock.
		By stockSelector = By.cssSelector(".stock .value");
		
		WebElement availabilityLabel = null;

	
		List<WebElement> stockElements = context.driver().findElements(stockSelector);
		Assert.assertTrue(stockElements.size() < 2);

		if(stockElements.size() == 1) {
			availabilityLabel = stockElements.get(0);
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

		By notificationBarCloseSelector = By.cssSelector("#bar-notification > .close");
		wait.until(ExpectedConditions.visibilityOfElementLocated(notificationBarCloseSelector)).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(notificationBarTextSelector));

		return quantity;
	}

	private void navigateToCart() {
		context.driver().findElement(By.cssSelector(SELECTOR_HEADER_MENU)).click();
		context.driver().findElement(By.cssSelector("#topcartlink a.ico-cart")).click();
	}

	private void removeItemsFromCart() {
		
		//WebDriverWait wait = new WebDriverWait(context.driver(), Duration.ofSeconds(10));
		
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

	private int getRandomInt(int min, int max) {
		Random random = new Random(System.currentTimeMillis());
		int range = (max - min) + 1;
		return min + random.nextInt(range);
	}
}



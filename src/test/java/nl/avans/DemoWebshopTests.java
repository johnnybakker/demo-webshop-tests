package nl.avans;


import nl.avans.data.ShopItem;
import org.openqa.selenium.By;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import nl.avans.context.TestContext;
import nl.avans.data.TestDataProvider;
import nl.avans.data.User;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
		WebDriverWait wait = new WebDriverWait(context.driver(), 10);
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
		open();
		homepage();
		User randomUser = getRandomUser(users);
		login(randomUser);
		//selectCategoryAndAddProductToCart("Electronics", "div.sub-category-grid div.item-box:nth-child(2) a[href=\"/cell-phones\"]","Smartphone", "addtocart_43.EnteredQuantity", "add-to-cart-button-43");
		//selectCategoryAndAddProductToCart("Apparel & Shoes","", "Blue Jeans", "addtocart_36.EnteredQuantity", "add-to-cart-button-36");
		List<ShopItem> items = ShopItem.getItems();

		ShopItem electronicsItem = items.get(0);
		selectCategoryAndAddProductToCart(electronicsItem, 1, 10);

		ShopItem apparelItem = items.get(1);
		selectCategoryAndAddProductToCart(apparelItem, 4, 5);

		navigateToCart();
		removeItemsFromCart();
	}

	private List<User> readUsers(String filename) throws Exception {
		List<User> users = TestDataProvider.instance.readTestData(filename, User.class);
		Assert.assertNotEquals(0, users.size());
		return users;
	}

	private User getRandomUser(List<User> users) {
		Random random = new Random();
		return users.get(random.nextInt(users.size()));
	}

	private void selectCategoryAndAddProductToCart(ShopItem item, int low, int max) {
		WebDriverWait wait = new WebDriverWait(context.driver(), 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.linkText(item.getCategory()))).click();
		if (!item.getSubCategorySelector().isEmpty()) {
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(item.getSubCategorySelector()))).click();
		}
		wait.until(ExpectedConditions.elementToBeClickable(By.linkText(item.getProductName()))).click();
		WebElement quantityInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name(item.getQuantityInputId())));
		quantityInput.clear();
		Random rand = new Random();
		int randomNum = rand.nextInt((max - low) + 1) + low;
		quantityInput.sendKeys(String.valueOf(RandomNumbers(low, max)));
		JavascriptExecutor executor = (JavascriptExecutor) context.driver();
		executor.executeScript("$('#" + item.getQuantityInputId() + "').keydown(function(event) { if (event.keyCode == 13) { $('#" + item.getAddToCartButtonId() + "').click(); return false; } });");
		wait.until(ExpectedConditions.elementToBeClickable(By.id(item.getAddToCartButtonId()))).click();
		//String addedToCartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(BAR_NOTIFICATION))).getText();
		//Assert.assertTrue(addedToCartMessage.contains("The product has been added to your shopping cart"));
	}

	public int RandomNumbers(int low, int max) {
		if (max < low) {
			low = 2;
			max = 12;
		}

		if (low < 0 || max < 0) {
			low = 2;
			max = 12;

		}else{
			Random rand = new Random();
			int randomNum = rand.nextInt((max - low) + 1) + low;
			return randomNum;
		}
		return low;
	}



	private void navigateToCart() {
		context.driver().findElement(By.cssSelector(SELECTOR_HEADER_MENU)).click();
		context.driver().findElement(By.cssSelector("#topcartlink a.ico-cart")).click();
	}

	private void removeItemsFromCart() {
		WebDriverWait wait = new WebDriverWait(context.driver(), 10);
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



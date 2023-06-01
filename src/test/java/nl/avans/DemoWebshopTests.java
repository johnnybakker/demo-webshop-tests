package nl.avans;


import org.openqa.selenium.By;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import nl.avans.data.User;
import java.util.List;

public class DemoWebshopTests 
{
	private final String PATH_LOGIN = "/login";

	private final String WEBSHOP_URL = "https://demowebshop.tricentis.com";
	private final String WEBSHOP_URL_LOGIN = WEBSHOP_URL + PATH_LOGIN;
	
	private final String WEBSHOP_TITLE = "Demo Web Shop";
	private final String WEBSHOP_TITLE_LOGIN = WEBSHOP_TITLE + ". Logi";

	private final String SELECTOR_HEADER_LINKS = ".header-links";
	
	private final String SELECTOR_LOGIN_LINK = SELECTOR_HEADER_LINKS + " [href=\""+PATH_LOGIN+"\"]";
	private final String SELECTOR_ACCOUNT_LINK = SELECTOR_HEADER_LINKS + " [href=\"/customer/info\"]";
	private final String SELECTOR_LOGOUT_LINK = SELECTOR_HEADER_LINKS + " [href=\"/logout\"]";
	
	private final String SELECTOR_LOGIN_FORM = "form[action=\""+PATH_LOGIN+"\"]";
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
			login(user.email, user.password);
			// Logout user
			logout();
		}
	}

	public void open() throws Exception {
        context.driver().get(WEBSHOP_URL);
    }

    public void homepage() throws Exception {
        String title = context.driver().getTitle();
        Assert.assertEquals(WEBSHOP_TITLE, title);
    }

	public void login(String email, String password) throws Exception {
		open();

		context.driver().findElement(By.cssSelector(SELECTOR_LOGIN_LINK)).click();

		Assert.assertEquals(WEBSHOP_URL_LOGIN, context.driver().getCurrentUrl());
		Assert.assertEquals(WEBSHOP_TITLE_LOGIN, context.driver().getTitle());

		context.driver().findElement(By.cssSelector(SELECTOR_LOGIN_FORM_EMAIL)).sendKeys(email);
		context.driver().findElement(By.cssSelector(SELECTOR_LOGIN_FORM_PASSWORD)).sendKeys(password);
		context.driver().findElement(By.cssSelector(SELECTOR_LOGIN_FORM_SUBMIT)).click();

		homepage();

		Assert.assertEquals(email, context.driver().findElement(By.cssSelector(SELECTOR_ACCOUNT_LINK)).getText());
	}

	public void logout() {
		context.driver().findElement(By.cssSelector(SELECTOR_LOGOUT_LINK)).click();
	}
}

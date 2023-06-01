package nl.avans;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;

import nl.avans.data.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;
import java.util.List;

public class DemoWebshopTests 
{
	private final String PATH_LOGIN = "/login";

	private final String WEBSHOP_URL = "https://demowebshop.tricentis.com";
	private final String WEBSHOP_URL_LOGIN = WEBSHOP_URL + PATH_LOGIN;
	
	private final String WEBSHOP_TITLE = "Demo Web Shop";
	private final String WEBSHOP_TITLE_LOGIN = WEBSHOP_TITLE + ". Login";

	private final String SELECTOR_HEADER_LINKS = ".header-links";
	
	private final String SELECTOR_LOGIN_LINK = SELECTOR_HEADER_LINKS + " [href=\""+PATH_LOGIN+"\"]";
	private final String SELECTOR_ACCOUNT_LINK = SELECTOR_HEADER_LINKS + " [href=\"/customer/info\"]";
	private final String SELECTOR_LOGOUT_LINK = SELECTOR_HEADER_LINKS + " [href=\"/logout\"]";
	
	private final String SELECTOR_LOGIN_FORM = "form[action=\""+PATH_LOGIN+"\"]";
	private final String SELECTOR_LOGIN_FORM_EMAIL = SELECTOR_LOGIN_FORM + " #Email";
	private final String SELECTOR_LOGIN_FORM_PASSWORD = SELECTOR_LOGIN_FORM + " #Password";
	private final String SELECTOR_LOGIN_FORM_SUBMIT = SELECTOR_LOGIN_FORM + " input[type=\"submit\"]";


	private WebDriver driver = null;

	@RegisterExtension
    public TestWatcher watcher = new TestWatcher() {
		@Override
        public void testSuccessful(ExtensionContext context) {
            endSession("passed");
        }

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            endSession("failed");
        }

        private void endSession(String status) {
			if(driver instanceof RemoteWebDriver) {
				((RemoteWebDriver)driver).executeScript("sauce:job-result=" + status);
			}
        }	
	};

    @BeforeEach
    public void setUp() throws Exception {

		// Create driver
		driver = WebDriverFactory.Create();
	
		// Set the implicit timeout to 500 milliseconds
		Duration implicityWait = Duration.ofMillis(500);
		driver.manage().timeouts().implicitlyWait(implicityWait);
    }

    @AfterEach
    public void tearDown() throws Exception {
		driver.quit();
		driver = null;
    }

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
		assertNotEquals(0, users.size());

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
        driver.get(WEBSHOP_URL);
    }

    public void homepage() throws Exception {
        String title = driver.getTitle();
        assertEquals(WEBSHOP_TITLE, title);
    }

	public void login(String email, String password) throws Exception {
		open();

		driver.findElement(By.cssSelector(SELECTOR_LOGIN_LINK)).click();

		assertEquals(WEBSHOP_URL_LOGIN, driver.getCurrentUrl());
		assertEquals(WEBSHOP_TITLE_LOGIN, driver.getTitle());

		driver.findElement(By.cssSelector(SELECTOR_LOGIN_FORM_EMAIL)).sendKeys(email);
		driver.findElement(By.cssSelector(SELECTOR_LOGIN_FORM_PASSWORD)).sendKeys(password);
		driver.findElement(By.cssSelector(SELECTOR_LOGIN_FORM_SUBMIT)).click();

		homepage();

		assertEquals(email, driver.findElement(By.cssSelector(SELECTOR_ACCOUNT_LINK)).getText());
	}

	public void logout() {
		driver.findElement(By.cssSelector(SELECTOR_LOGOUT_LINK)).click();
	}
}

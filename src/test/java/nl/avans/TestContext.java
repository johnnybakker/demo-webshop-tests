package nl.avans;

import java.time.Duration;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;

public abstract class TestContext extends TestWatcher {

	protected static final String ENV_TEST_PLATFORM_NAME = "TEST_PLATFORM_NAME";
	protected static final String ENV_TEST_BROWSER_NAME = "TEST_BROWSER_NAME";
	protected static final String ENV_TEST_BROWSER_VERSION = "TEST_BROWSER_VERSION";

	protected static final String PLATFORM_NAME;
	protected static final String BROWSER_NAME;
	protected static final String BROWSER_VERSION;

	static {
		String platformName = System.getenv(ENV_TEST_PLATFORM_NAME);
		PLATFORM_NAME = (platformName != null && !platformName.isEmpty()) ? platformName : "Windows 11";

		String browserName = System.getenv(ENV_TEST_BROWSER_NAME);
		BROWSER_NAME = (browserName != null && !browserName.isEmpty()) ? browserName : "chrome";

		String browserVersion= System.getenv(ENV_TEST_BROWSER_VERSION);
		BROWSER_VERSION = (browserVersion != null && !browserVersion.isEmpty()) ? browserVersion : "latest";
	}

	public static TestContext Create() {
		try {
			// Try to create a saucelabs context
			return new SaucelabsTestContext();
		} catch(Exception ex) {
			// TODO: ADD OTHER BROWSER DRIVER OPTIONS
			// Fallback options is local chrome driver on windows 11 platform
			// This will be used for developer tests
			return new LocalTestContext();
		}
	}

	@Override
	protected void starting(Description description) {
		// Set the implicit timeout to 500 milliseconds
		Duration implicityWait = Duration.ofMillis(500);

		var driverOptions = driver().manage();
		driverOptions.timeouts().implicitlyWait(implicityWait);
		driverOptions.deleteAllCookies();
	}

	@Override
	protected void finished(Description description) {
		driver().quit();
	}

	@Override
	protected void failed(Throwable e, Description description) {}
	
	@Override
	protected void succeeded(Description description) {}

	public abstract WebDriver driver();

}

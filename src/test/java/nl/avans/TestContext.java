package nl.avans;

import org.openqa.selenium.WebDriver;

public abstract class TestContext {

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

	public abstract WebDriver getDriver();
	public abstract void destroy();

}

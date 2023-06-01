package nl.avans;

import java.net.URL;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.junit.runner.Description;

public class SaucelabsTestContext extends TestContext {

	public static final String ENV_SAUCELABS_NAME = "SAUCELABS_NAME";
	public static final String ENV_SAUCELABS_USERNAME = "SAUCELABS_USERNAME";
	public static final String ENV_SAUCELABS_ACCESS_KEY = "SAUCELABS_ACCESS_KEY";
	public static final String ENV_SAUCELABS_BUILD = "SAUCELABS_BUILD";
	public static final String ENV_SAUCELABS_URL = "SAUCELABS_URL";

	private RemoteWebDriver _driver;

	public SaucelabsTestContext() throws Exception {
		SauceLabsDriverOptions options = SauceLabsDriverOptions.ReadFromEnvironment();
	
		MutableCapabilities capabilities = new MutableCapabilities();
		capabilities.setCapability(CapabilityType.PLATFORM_NAME, PLATFORM_NAME);
		capabilities.setCapability(CapabilityType.BROWSER_NAME, BROWSER_NAME);
		capabilities.setCapability(CapabilityType.BROWSER_VERSION, BROWSER_VERSION);
		capabilities.setCapability("sauce:options", new HashMap<>(){{ 
			put("username", options.username);
			put("accessKey", options.accessKey);
			put("build", options.build);
			put("name", options.name);
		}});

		URL url = new URL(options.url);
		_driver = new RemoteWebDriver(url, capabilities);
	}

	@Override
	public WebDriver driver() {
		return _driver;
	}

	@Override
	protected void failed(Throwable e, Description description) {
		_driver.executeScript("sauce:job-result=failed");
	}

	@Override
	protected void succeeded(Description description) {
		_driver.executeScript("sauce:job-result=passed");
	}
	
	static class SauceLabsDriverOptions {

		public String name;
		public String username;
		public String accessKey;
		public String build;
		public String url;

		public static SauceLabsDriverOptions ReadFromEnvironment() throws Exception {
			SauceLabsDriverOptions options = new SauceLabsDriverOptions();
			options.name = GetEnvOrThrowFailedToFind(ENV_SAUCELABS_NAME);
			options.username = GetEnvOrThrowFailedToFind(ENV_SAUCELABS_USERNAME);
			options.accessKey = GetEnvOrThrowFailedToFind(ENV_SAUCELABS_ACCESS_KEY);
			options.build = GetEnvOrThrowFailedToFind(ENV_SAUCELABS_BUILD);
			options.url = GetEnvOrThrowFailedToFind(ENV_SAUCELABS_URL);
			return options;
		}

		private static String GetEnvOrThrowFailedToFind(String key) throws Exception {
			String value = System.getenv(key);
			if(value == null) ThrowFailedToFind(key);
			return value;
		}
	
		private static void ThrowFailedToFind(String key) throws Exception {
			throw new Exception("Failed to find environment key " + key);
		}	
	}
}

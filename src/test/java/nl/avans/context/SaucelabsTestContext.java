package nl.avans.context;

import java.net.URL;
import java.util.HashMap;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import nl.avans.data.TestDataProvider;

import org.junit.runner.Description;

public class SaucelabsTestContext extends TestContext {

	public static final String ENV_SAUCELABS_NAME = "SAUCELABS_NAME";
	public static final String ENV_SAUCELABS_USERNAME = "SAUCELABS_USERNAME";
	public static final String ENV_SAUCELABS_ACCESS_KEY = "SAUCELABS_ACCESS_KEY";
	public static final String ENV_SAUCELABS_BUILD = "SAUCELABS_BUILD";
	public static final String ENV_SAUCELABS_URL = "SAUCELABS_URL";

	private SauceLabsDriverOptions _options;
	private RemoteWebDriver _driver;

	public SaucelabsTestContext(TestDataProvider provider) throws Exception {
		super(provider);
		_options = SauceLabsDriverOptions.ReadFromEnvironment();
		_driver = null;
	}

	@Override
	public WebDriver driver() {
		return _driver;
	}

	@Override
	protected void starting(Description description) {

		String name = description.getClassName() + "." + description.getMethodName();

		MutableCapabilities capabilities = new MutableCapabilities();
		capabilities.setCapability(CapabilityType.PLATFORM_NAME, PLATFORM_NAME);
		capabilities.setCapability(CapabilityType.BROWSER_NAME, BROWSER_NAME);
		capabilities.setCapability(CapabilityType.BROWSER_VERSION, BROWSER_VERSION);
		capabilities.setCapability("sauce:options", new HashMap<>(){{ 
			put("username", _options.username);
			put("accessKey", _options.accessKey);
			put("build", _options.build);
			put("name", name);
		}});

		_driver = new RemoteWebDriver(_options.url, capabilities);
	}

	@Override
	protected void failed(Throwable e, Description description) {
		System.out.println("Sending result failed!");
		_driver.executeScript("sauce:job-result=failed");
	}

	@Override
	protected void succeeded(Description description) {
		System.out.println("Sending result passed!");
		_driver.executeScript("sauce:job-result=passed");
	}
	
	static class SauceLabsDriverOptions {

		public String username;
		public String accessKey;
		public String build;
		public URL url;

		public static SauceLabsDriverOptions ReadFromEnvironment() throws Exception {
			SauceLabsDriverOptions options = new SauceLabsDriverOptions();
			options.username = GetEnvOrThrowFailedToFind(ENV_SAUCELABS_USERNAME);
			options.accessKey = GetEnvOrThrowFailedToFind(ENV_SAUCELABS_ACCESS_KEY);
			options.build = GetEnvOrThrowFailedToFind(ENV_SAUCELABS_BUILD);
			options.url = new URL(GetEnvOrThrowFailedToFind(ENV_SAUCELABS_URL));
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

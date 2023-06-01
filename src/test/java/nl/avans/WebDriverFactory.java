package nl.avans;

import java.net.URL;
import java.util.HashMap;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebDriverFactory {

	public static final String ENV_TEST_PLATFORM_NAME = "TEST_PLATFORM_NAME";
	public static final String ENV_TEST_BROWSER_NAME = "TEST_BROWSER_NAME";
	public static final String ENV_TEST_BROWSER_VERSION = "TEST_BROWSER_VERSION";
	public static final String ENV_SAUCELABS_NAME = "SAUCELABS_NAME";
	public static final String ENV_SAUCELABS_USERNAME = "SAUCELABS_USERNAME";
	public static final String ENV_SAUCELABS_ACCESS_KEY = "SAUCELABS_ACCESS_KEY";
	public static final String ENV_SAUCELABS_BUILD = "SAUCELABS_BUILD";
	public static final String ENV_SAUCELABS_URL = "SAUCELABS_URL";

	private static final String PLATFORM_NAME;
	private static final String BROWSER_NAME;
	private static final String BROWSER_VERSION;

	static {
		String platformName = System.getenv(ENV_TEST_PLATFORM_NAME);
		PLATFORM_NAME = (platformName != null && !platformName.isEmpty()) ? platformName : "Windows 11";

		String browserName = System.getenv(ENV_TEST_BROWSER_NAME);
		BROWSER_NAME = (browserName != null && !browserName.isEmpty()) ? browserName : "chrome";

		String browserVersion= System.getenv(ENV_TEST_BROWSER_VERSION);
		BROWSER_VERSION = (browserVersion != null && !browserVersion.isEmpty()) ? browserVersion : "latest";
	}
	
	public static WebDriver Create() {

		try {

			// Try to create a sauce labs web driver 
			return CreateSauceLabsDriver();

		} catch(Exception ex) {
			// TODO: ADD OTHER BROWSER DRIVER OPTIONS
			// Fallback options is local chrome driver on windows 11 platform
			// This will be used for developer tests
			ChromeOptions options = new ChromeOptions();
			options.setPlatformName(PLATFORM_NAME);
			options.setBrowserVersion(BROWSER_VERSION);
			options.addArguments("--remote-allow-origins=*");
			return new ChromeDriver(options);
		}
	}

	private static WebDriver CreateSauceLabsDriver() throws Exception {

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
		return new RemoteWebDriver(url, capabilities);
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

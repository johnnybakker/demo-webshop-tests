package nl.avans;

import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LocalTestContext extends TestContext {

	private WebDriver _driver;

	public LocalTestContext() {
		ChromeOptions options = new ChromeOptions();
		options.setPlatformName(PLATFORM_NAME);
		options.setBrowserVersion(BROWSER_VERSION);
		options.addArguments("--remote-allow-origins=*");
		_driver = new ChromeDriver(options);
	}

	@Override
	public WebDriver driver() {
		return _driver;
	}
}

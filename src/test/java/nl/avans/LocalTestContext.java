package nl.avans;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LocalTestContext extends TestContext {

	WebDriver driver;

	public LocalTestContext() {
		ChromeOptions options = new ChromeOptions();
		options.setPlatformName(PLATFORM_NAME);
		options.setBrowserVersion(BROWSER_VERSION);
		options.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(options);
	}

	@Override
	public WebDriver getDriver() {
		return driver;
	}

	@Override
	public void destroy() {
		driver.quit();
		driver = null;
	}
}

package nl.avans.context;

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
	protected void starting(Description description) {
		String name = description.getClassName() + "." + description.getMethodName();
		System.out.println("Starting test: " + name);
	}

	@Override
	protected void failed(Throwable e, Description description) {
		System.out.println("Failed!");
	}

	@Override
	protected void succeeded(Description description) {
		System.out.println("Succeeded!");
	}

	@Override
	public WebDriver driver() {
		return _driver;
	}
}

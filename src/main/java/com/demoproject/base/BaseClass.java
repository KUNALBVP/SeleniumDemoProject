package com.demoproject.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import com.demoproject.actiondriver.ActionDriver;
import com.demoproject.utilities.ExtentManager;
import com.demoproject.utilities.LoggerManager;

public class BaseClass {

	protected static Properties prop;
	// protected static WebDriver driver;
	// private static ActionDriver actionDriver;

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();

	protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);

	// getter method for soft assert
	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}

	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	@BeforeSuite
	public void loadconfig() throws IOException {
		// load the configuration file
		prop = new Properties();
		FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "/src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.properties file loaded");

		// Start the ExtentReport
		// ExtentManager.getReporter(); --this has been implemented in TestListener
	}

	@BeforeMethod
	@Parameters("browser")
	public synchronized void setup(String browser) throws IOException {
		System.out.println("Setting up WebDriver for: " + this.getClass().getSimpleName());
		launchBrowser(browser);
		configureBrowser();
		staticWait(3);

		logger.info("WebDriver initialized and Browser Maximised");
		logger.trace("TRACE message");
		logger.error("ERROR message");
		logger.fatal("FATAL message");
		logger.warn("WARN message");
		logger.debug("DEBUG message");
		logger.info("INFO message");
		/*
		 * //initialize the ActionDriver object only once if(actionDriver == null) {
		 * actionDriver = new ActionDriver(driver);
		 * logger.info("ActionDriver instance is created "+Thread.currentThread().getId(
		 * )); }
		 */
		// initialize the actionDriver for current thread
		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("Action driver thread is initialized: " + Thread.currentThread().getId());
	}

	private synchronized void launchBrowser(String browser) {
		// initialize the WebDriver based on browser defined in config.properties file
		//String browser = prop.getProperty("browser");

		boolean seleniumGrid = Boolean.parseBoolean(prop.getProperty("seleniumGrid"));
		String gridURL = prop.getProperty("gridURL");

		if (seleniumGrid) {
			try {
				if (browser.equalsIgnoreCase("chrome")) {
					ChromeOptions options = new ChromeOptions();
					options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080",
							"--disable-notifications", "--no-sandbox");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else if (browser.equalsIgnoreCase("firefox")) {
					FirefoxOptions options = new FirefoxOptions();
					options.addArguments("--headless");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else if (browser.equalsIgnoreCase("edge")) {
					EdgeOptions options = new EdgeOptions();
					options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-notifications");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else {
					throw new IllegalArgumentException("Browser not supported: " + browser);
				}
				logger.info("RemoteWebDriver instance created for grid in headless mode");
			} catch (MalformedURLException e) {
				throw new RuntimeException("Invalid Grid URL: ", e);
			}
		} else {
			if (browser.equalsIgnoreCase("chrome")) {

				// Create ChromeOptions
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless=new"); // run chrome in headless mode
				options.addArguments("--remote-allow-origins=*");
				options.addArguments("--disable-gpu"); // disable gpu for headless mode
				options.addArguments("--window-size=1920,1080"); // set window size
				options.addArguments("--disable-notifications"); // disable browser notifications
				options.addArguments("--no-sandbox"); // required for some CI environment
				options.addArguments("--disable-dev-shm-usage"); // resolve issues in resource sharing
				options.addArguments("--force-device-scale-factor=1");
				options.addArguments("--high-dpi-support=1");

				// driver = new ChromeDriver();
				driver.set(new ChromeDriver(options));
				ExtentManager.registerDriver(getDriver());
				logger.info("ChromeDriver instance is created");
			} else if (browser.equalsIgnoreCase("firefox")) {

				// Create FireFoxOptions
				FirefoxOptions options = new FirefoxOptions();
				options.addArguments("--headless"); // run firefox in headless mode
				options.addArguments("--disable-gpu"); // disable gpu for headless mode
				options.addArguments("--width=1920"); // set browser width
				options.addArguments("--height=1080"); // set browser height
				options.addArguments("--disable-notifications"); // disable browser notifications
				options.addArguments("--no-sandbox"); // required for some CI environment
				options.addArguments("--disable-dev-shm-usage"); // resolve issues in resource sharing
				// driver = new FirefoxDriver();
				driver.set(new FirefoxDriver(options));
				ExtentManager.registerDriver(getDriver());
				logger.info("FirefoxDriver instance is created");
			} else if (browser.equalsIgnoreCase("edge")) {

				// Create EdgeOptions
				EdgeOptions options = new EdgeOptions();
				options.addArguments("--headless"); // run edge in headless mode
				options.addArguments("--disable-gpu"); // disable gpu for headless mode
				options.addArguments("--window-size=1920,1080"); // set window size
				options.addArguments("--disable-notifications"); // disable browser notifications
				options.addArguments("--no-sandbox"); // required for some CI environment
				options.addArguments("--disable-dev-shm-usage"); // resolve issues in resource sharing

				// driver = new EdgeDriver();
				driver.set(new EdgeDriver(options));
				ExtentManager.registerDriver(getDriver());
				logger.info("EdgeDriver instance is created");
			} else {
				throw new IllegalArgumentException("Browser not supported:" + browser);
			}
		}
	}

	private void configureBrowser() {
		// implicitWait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

		// Maximize the browser
		// getDriver().manage().window().maximize();

		// navigate to URL
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("failed to navigate to URL:" + e.getMessage());
		}
	}

	@AfterMethod
	public synchronized void tearDown() {
		if (getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				System.out.println("unable to quit the driver:" + e.getMessage());
			}
		}
		logger.info("WebDriver instance is closed");
		// driver = null;
		// actionDriver = null;
		driver.remove();
		actionDriver.remove();
		// ExtentManager.endTest(); --this has been implemented in TestListener
	}

	// getter method for prop
	public static Properties getProp() {
		return prop;
	}

	// driver getter method
	/*
	 * public WebDriver getDriver() { return driver; }
	 */
	// new getter method ensuring singleton design pattern for WebDriver
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			System.out.println("WebDriver is not initialized");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get();
	}

	// new getter method ensuring singleton design pattern for ActionDriver
	public static ActionDriver getActionDriver() {
		if (actionDriver.get() == null) {
			System.out.println("ActionDriver is not initialized");
			throw new IllegalStateException("ActionDriver is not initialized");
		}
		return actionDriver.get();
	}

	// driver setter method
	public void setDriver(ThreadLocal<WebDriver> driver) {
		this.driver = driver;
	}

	// static wait for Pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}

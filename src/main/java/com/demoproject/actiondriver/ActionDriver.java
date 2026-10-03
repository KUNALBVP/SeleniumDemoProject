package com.demoproject.actiondriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.demoproject.base.BaseClass;
import com.demoproject.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;

	public static final Logger logger = BaseClass.logger;

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
		logger.info("WebDriver instance is created");
	}

	// Method to click an element
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		try {
			applyBorder(by, "green");
			waitForElementToBeClickable(by);
			driver.findElement(by).click();
			ExtentManager.logStep("Clicked an element: " + elementDescription);
			logger.info("Clicked an element-->" + elementDescription);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("unable to click the element: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click element: ",
					elementDescription + "_unable to click");
		}

	}

	// Method to enter text into input field
	public void enterText(By by, String value) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "green");
			driver.findElement(by).clear();
			driver.findElement(by).sendKeys(value);
			logger.info("Entered text on: " + getElementDescription(by) + " with " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("unable to enter the value: " + e.getMessage());
		}
	}

	// Method to get text from an input field
	public String getText(By by) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "green");
			return driver.findElement(by).getText();
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("unable to get the text: " + e.getMessage());
			return "";
		}
	}

	// Method to compare Two texts
	public boolean compareText(By by, String expectedText) {
		try {
			waitForElementToBeVisible(by);
			String actualText = driver.findElement(by).getText();
			if (expectedText.equals(actualText)) {
				applyBorder(by, "green");
				logger.info("Text are matching: " + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Compare text",
						"Text verified successfully! " + actualText + " equals " + expectedText);
				return true;
			} else {
				applyBorder(by, "red");
				logger.error("Text are not matching: " + actualText + " not equals " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Text comparison failed!",
						"Text comparison failed! " + actualText + " not equals " + expectedText);
				return false;
			}
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("unable to compare texts: " + e.getMessage());

		}
		return false;
	}

	// Method to check if an element is Displayed
	public boolean isDisplayed(By by) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "green");
			logger.info("Element is displayed " + getElementDescription(by));
			ExtentManager.logStep("Element is displayed: " + getElementDescription(by));
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is displayed",
					"Element is displayed: " + getElementDescription(by));
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Element is not displayed: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Element is not displayed: ",
					"Element is not displayed" + getElementDescription(by));
			return false;
		}
	}

	// wait for page to load
	public void waitForPageLoad(int timeOutInSec) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> ((JavascriptExecutor) WebDriver)
					.executeScript("return document.readyState").equals("complete"));
			logger.info("Page loaded successfully");
		} catch (Exception e) {
			logger.error("Page did not load within " + timeOutInSec + " seconds. Exception: " + e.getMessage());
		}
	}

	// scroll to an element
	public void scrollToElement(By by) {
		try {
			applyBorder(by, "green");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("argument[0],scrollIntoView(true);", element);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to locate an element: " + e.getMessage());
		}
	}

	// wait for element to be clickable
	private void waitForElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("element is not clickable: " + e.getMessage());
		}
	}

	// wait for element to be visible
	private void waitForElementToBeVisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("element is not visible: " + e.getMessage());
		}
	}

	// Method to get the description of element for better logging
	public String getElementDescription(By locator) {
		// check for null driver or locator to avoid null pointer exception
		if (driver == null) {
			return "driver is null";
		}
		if (locator == null) {
			return "locator is null";
		}

		try {
			// find the element using locator
			WebElement element = driver.findElement(locator);

			// Get element description
			String name = element.getDomAttribute("name");
			String placeHolder = element.getDomAttribute("placeholder");
			String id = element.getDomAttribute("id");
			String className = element.getDomAttribute("class");
			String text = element.getText();

			// return the description based on element attribute
			if (isNotEmpty(name)) {
				return "Element with name: " + name;
			} else if (isNotEmpty(id)) {
				return "Element with id: " + id;
			} else if (isNotEmpty(text)) {
				return "Element with text: " + truncate(text, 50);
			} else if (isNotEmpty(className)) {
				return "Element with class: " + className;
			} else if (isNotEmpty(placeHolder)) {
				return "Element with placeholder: " + placeHolder;
			}
		} catch (Exception e) {
			logger.error("Unable to describe the element " + e.getMessage());
		}
		return "unable to describe the element";

	}

	// utility method to check String is not null or empty
	private boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	// utility method to truncate long string
	private String truncate(String value, int maxLength) {
		if (value == null || value.length() <= maxLength) {
			return value;
		}
		return value.substring(0, maxLength) + "...";

	}

	// utility method to border an element
	public void applyBorder(By by, String color) {
		try {
			WebElement element = driver.findElement(by);
			String script = "arguments[0].style.border='3px solid " + color + "'";
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(script, element);
			logger.info("Applied the border with color: " + color + " to element " + getElementDescription(by));
		} catch (Exception e) {
			logger.warn("Failed to apply the border to an element " + getElementDescription(by), e.getMessage());
		}
	}

	// =================================SELECT
	// METHODS=====================================

	// Method to select a dropdown by visible text
	public void selectByVisibleText(By by, String value) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByVisibleText(value);
			applyBorder(by, "green");
			logger.info("Selected dropdown value: " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown value: " + value, e);
		}

	}

	// Method to select a dropdown by value
	public void selectByValue(By by, String value) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByValue(value);
			applyBorder(by, "green");
			logger.info("Selected dropdown value by actual value: " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown by value: " + value, e);
		}

	}

	// Method to select a dropdown by index
	public void selectByIndex(By by, int index) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByIndex(index);
			applyBorder(by, "green");
			logger.info("Selected dropdown value by index: " + index);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown value by index: " + index, e);
		}

	}

	// Method to get all options from a dropdown
	public List<String> getDropdownOptions(By by) {
		List<String> optionsList = new ArrayList<>();
		try {
			WebElement dropdownElement = driver.findElement(by);
			Select select = new Select(dropdownElement);
			for (WebElement option : select.getOptions()) {
				optionsList.add(option.getText());
			}
			applyBorder(by, "green");
			logger.info("Retrieved dropdown options for: " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to get dropdown options: " + e.getMessage());
		}
		return optionsList;
	}

	// ====================== JavaScript Utility Methods
	// ===================================

	// Method to click using javascript
	public void clickUsingJs(By by) {
		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
			applyBorder(by, "green");
			logger.info("Clicked element using Javascript: " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to click using javascript", e);
		}
	}

	// Method to scroll to the bottom of the page
	public void scrollToBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
		logger.info("Scrolled to the Bottom of the page");
	}

	// Method to highlight an element using Javascript
	public void highlightElementJS(By by) {
		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].style.border = '3px solid red';", element);
			logger.info("Highlighted element using Javascript: " + getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to highlight element using javascript", e);
		}
	}

	// ================================Window and Frame
	// Handling====================================

	// Method to switch between browser windows
	public void switchToWindows(String windowTitle) {
		try {
			Set<String> windows = driver.getWindowHandles();
			for (String window : windows) {
				driver.switchTo().window(window);
				if (driver.getTitle().equals(windowTitle)) {
					logger.info("Switched to window: " + windowTitle);
					return;
				}

			}
			logger.warn("Window with title: " + windowTitle + " is not found");
		} catch (Exception e) {
			logger.error("Unable to switch window", e);
		}

	}

	// Method to switch to an IFrame
	public void switchToFrame(By by) {
		try {
			driver.switchTo().frame(driver.findElement(by));
			logger.info("Switched to iframe: " + getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to switch to iframe", e);
		}
	}

	// Method to switch back to default content
	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
		logger.info("Switched back to default content");
	}

	// ==============================Alert
	// Handling===========================================

	// Method to accept an alert pop up
	public void acceptAlert() {
		try {
			driver.switchTo().alert().accept();
			logger.info("Alert accepted");
		} catch (Exception e) {
			logger.error("No alert found to accept", e);
		}
	}

	// Method to dismiss an alert pop up
	public void dismissAlert() {
		try {
			driver.switchTo().alert().dismiss();
			logger.info("Alert dismissed");
		} catch (Exception e) {
			logger.error("No alert found to dismiss", e);
		}
	}

	// Method to get alert text
	public String getAlertText() {
		try {
			return driver.switchTo().alert().getText();
		} catch (Exception e) {
			logger.error("No alert text found", e);
			return "";
		}
	}

	// ==============================Browser
	// Actions======================================

	public void refreshPage() {
		try {
			driver.navigate().refresh();
			ExtentManager.logStep("Page refreshed successfully");
			logger.info("Page refreshed successfully");
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to refresh page", "refresh failed");
			logger.error("Unable to refresh page: " + e.getMessage());
		}
	}

	public String getCurrentURL() {
		try {
			String url = driver.getCurrentUrl();
			ExtentManager.logStep("Current URL fetched: " + url);
			logger.info("Current URL fetched: " + url);
			return url;
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to fetch current URL",
					"Fetch current URL task failed");
			logger.error("Unable to fetch current url: " + e.getMessage());
			return null;
		}
	}

	public void maximizeWindow() {
		try {
			driver.manage().window().maximize();
			ExtentManager.logStep("Browser window maximized");
			logger.info("Browser window maximized");
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to maximize window", "Maximize window task failed");
			logger.error("Unable to maximize window: " + e.getMessage());
		}
	}

	// ============================Advanced WebElement
	// Actions================================

	public void moveToElement(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(driver.findElement(by)).perform();
			ExtentManager.logStep("Moved to element: " + elementDescription);
			logger.info("Moved to element -->" + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to move to an element",
					"Move to element task failed");
			logger.error("Unable to move to element: " + e.getMessage());
		}
	}

	public void dragAndDrop(By source, By target) {
		String sourceDescription = getElementDescription(source);
		String targetDescription = getElementDescription(target);

		try {
			Actions actions = new Actions(driver);
			actions.dragAndDrop(driver.findElement(source), driver.findElement(target)).perform();
			ExtentManager.logStep("Dragged element: " + sourceDescription + " and Dropped on: " + targetDescription);
			logger.info("Dragged element: " + sourceDescription + " and Dropped on: " + targetDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to drag and drop", sourceDescription);
			logger.error("Unable to drag and drop" + e.getMessage());
		}
	}

	public void doubleClick(By by) {
		String elementDescription = getElementDescription(by);

		try {
			Actions actions = new Actions(driver);
			actions.doubleClick(driver.findElement(by)).perform();
			ExtentManager.logStep("Double clicked on element: " + elementDescription);
			logger.info("Double clicked on element: " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to double click the element", elementDescription);
			logger.error("Unable to double click element: " + e.getMessage());
		}
	}

	public void rightClick(By by) {
		String elementDescription = getElementDescription(by);

		try {
			Actions actions = new Actions(driver);
			actions.contextClick(driver.findElement(by)).perform();
			ExtentManager.logStep("Right-clicked on element: " + elementDescription);
			logger.info("Right-clicked on element: " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to right click the element", elementDescription);
			logger.error("Unable to right click on element: " + e.getMessage());
		}
	}

	public void sendKeysWithActions(By by, String value) {
		String elementDescription = getElementDescription(by);

		try {
			Actions actions = new Actions(driver);
			actions.sendKeys(driver.findElement(by), value).perform();
			ExtentManager.logStep("Sent keys to element: " + elementDescription + " | value: " + value);
			logger.info("Sent keys to element: " + elementDescription + " | value: " + value);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to send keys", elementDescription);
			logger.error("Unable to sent keys to an element: " + e.getMessage());
		}
	}

	public void clearText(By by) {
		String elementDescription = getElementDescription(by);

		try {
			driver.findElement(by).clear();
			ExtentManager.logStep("Cleared text from element: " + elementDescription);
			logger.info("Cleared text from element: " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to clear text from element", elementDescription);
			logger.error("Unable to clear text from an element: " + e.getMessage());
		}
	}

	// Method to upload a file
	public void uploadFile(By by, String filepath) {
		try {
			driver.findElement(by).sendKeys(filepath);
			applyBorder(by, "green");
			logger.info("Uploaded file: " + filepath);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to upload the file: " + e.getMessage());
		}
	}
	
	

}

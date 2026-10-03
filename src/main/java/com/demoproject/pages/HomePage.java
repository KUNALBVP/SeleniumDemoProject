package com.demoproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.demoproject.actiondriver.ActionDriver;
import com.demoproject.base.BaseClass;

public class HomePage {

	private ActionDriver actionDriver;

	// define locators using By class
	private By adminTab = By.xpath("//span[text()='Admin']");
	private By loggedInUserName = By.xpath("//p[@class='oxd-userdropdown-name']");
	private By logoutButton = By.linkText("Logout");
	private By logo = By.xpath("//img[@alt='client brand banner']");

	// Initializing ActionDriver object by passing WebDriver instance
	/*public HomePage(WebDriver driver) {
		this.actionDriver = new ActionDriver(driver);
	}*/
	public HomePage(WebDriver driver)
	{
		this.actionDriver = BaseClass.getActionDriver();
	}

	// Method to verify if Admin tab is visible
	public boolean isAdminTabVisible() {
		return actionDriver.isDisplayed(adminTab);
	}

	// method to verify if companyLogo is visible
	public boolean isLogoVisible() {
		return actionDriver.isDisplayed(logo);
	}
	
	//Method to perform logOut operation
	public void logOut()
	{
		actionDriver.click(loggedInUserName);
		actionDriver.click(logoutButton);
	}

}

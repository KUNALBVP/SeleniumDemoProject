package com.demoproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.demoproject.actiondriver.ActionDriver;
import com.demoproject.base.BaseClass;

public class LoginPage {

	private ActionDriver actionDriver;

	// Define locators using by class
	private By usernameField = By.name("username");
	private By passwordField = By.cssSelector("input[type='password']");
	private By loginButton = By.xpath("//button[text()= ' Login ']");
	private By errorMessage = By.xpath("//p[text()= 'Invalid credentials']");

	//Initializing ActionDriver object by passing WebDriver instance
	/*public LoginPage(WebDriver driver) {
		this.actionDriver = new ActionDriver(driver);
	}*/
	//code change to apply singleton design pattern. ActionDriver instance is now created only once
	public LoginPage(WebDriver driver)
	{
		this.actionDriver = BaseClass.getActionDriver();
	}

	// Method to perform login
	public void login(String userName, String password) {
		actionDriver.enterText(usernameField, userName);
		actionDriver.enterText(passwordField, password);
		actionDriver.click(loginButton);
	}

	// Method to check if error message is displayed
	public boolean isErrorDisplayed() {
		return actionDriver.isDisplayed(errorMessage);
	}

	// Method to get the text from Error Message
	public String getErrorText() {
		return actionDriver.getText(errorMessage);
	}

	// Verify if error is correct or not
	public boolean isErrorCorrect(String expectedError) {
		return actionDriver.compareText(errorMessage, expectedError);
	}
}

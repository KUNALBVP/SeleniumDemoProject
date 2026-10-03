package com.demoproject.test;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.demoproject.base.BaseClass;
import com.demoproject.pages.HomePage;
import com.demoproject.pages.LoginPage;
import com.demoproject.utilities.DataProviders;
import com.demoproject.utilities.ExtentManager;

public class HomePageTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	@Test(dataProvider="validLoginData", dataProviderClass=DataProviders.class)
	public void verifyOrangeHRMLogo(String username, String password) {
		//ExtentManager.startTest("Verify Logo Test");  --this has been implemented in TestListener
		ExtentManager.logStep("Navigating to Login Page for entering username and password");
		loginPage.login(username, password);
		ExtentManager.logStep("Verifying OrangeHRM logo is visible or not");
		Assert.assertTrue(homePage.isLogoVisible(), "OrangeHRM logo is NOT visible");
		ExtentManager.logStep("Validation Successful");
		homePage.logOut();
		ExtentManager.logStep("Logged out successfully!");
		staticWait(2);

	}
}

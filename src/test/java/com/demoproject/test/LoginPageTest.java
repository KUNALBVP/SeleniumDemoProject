package com.demoproject.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.demoproject.base.BaseClass;
import com.demoproject.pages.HomePage;
import com.demoproject.pages.LoginPage;
import com.demoproject.utilities.DataProviders;
import com.demoproject.utilities.ExtentManager;

public class LoginPageTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	@Test(dataProvider="validLoginData", dataProviderClass=DataProviders.class)
	public void validLoginTest(String username, String password) {
		//ExtentManager.startTest("Valid Login Test");  --this has been implemented in TestListener
		ExtentManager.logStep("Navigating to Login Page for entering username and password");
		loginPage.login(username, password);
		ExtentManager.logStep("Verifying Admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(), "Admin tab should be visible after successful login");
		ExtentManager.logStep("Validation Successful");
		homePage.logOut();
		ExtentManager.logStep("Logged out successfully!");
		staticWait(2);

	}

	@Test(dataProvider="invalidLoginData", dataProviderClass=DataProviders.class)
	public void invalidLoginTest(String username, String password) {
		SoftAssert softAssert = getSoftAssert();
		//ExtentManager.startTest("Invalid Login Test");  --this has been implemented in TestListener
		ExtentManager.logStep("Navigating to Login Page for entering username and password");
		loginPage.login(username, password);
		String expectedErrorMessage = "Invalid credentials";
		softAssert.assertTrue(loginPage.isErrorCorrect(expectedErrorMessage), "Test failed: Invalid error message");
		//Assert.assertTrue(loginPage.isErrorCorrect(expectedErrorMessage), "Test failed: Invalid error message");
		ExtentManager.logStep("Validation Successful");
		ExtentManager.logStep("Logged out successfully!");
		staticWait(3);
		softAssert.assertAll();
	}
}

package com.demoproject.test;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.demoproject.utilities.APIUtility;
import com.demoproject.utilities.ExtentManager;
import com.demoproject.utilities.RetryAnalyzer;

import io.restassured.response.Response;

public class ApiTest {

	//@Test(retryAnalyzer = RetryAnalyzer.class)
	@Test
	public void verifyGetUserAPI() {
		SoftAssert softAssert = new SoftAssert();

		// Step1: Define API Endpoint
		String endpoint = "https://jsonplaceholder.typicode.com/users/1";
		ExtentManager.logStep("API Endpoint: " + endpoint);

		// Step2: Send GET Request
		ExtentManager.logStep("Sending the GET request to API");
		Response response = APIUtility.sendGETRequest(endpoint);

		// Step3: Validate status Code
		ExtentManager.logStep("Validating API Response status Code");
		boolean isStatusCodeValid = APIUtility.validateResponseCode(response, 200);
		softAssert.assertTrue(isStatusCodeValid, "Status Code is not as expected");

		if (isStatusCodeValid) {
			ExtentManager.logStepValidationForAPI("Status Code Validation Passed!");
		} else {
			ExtentManager.logFailureAPI("Status Code Validation Failed");
		}

		// Step 4: Validate Username from the JSON Response
		ExtentManager.logStep("Validating response body for Username");
		String username = APIUtility.getJSONValue(response, "username");
		boolean isUsernameValid = "Bret".equals(username);
		softAssert.assertTrue(isUsernameValid, "Username is NOT valid");
		if (isUsernameValid) {
			ExtentManager.logStepValidationForAPI("Username Validation Passed!");
		} else {
			ExtentManager.logFailureAPI("Username Validation Failed");
		}

		// Step 5: Validate EmailId from the JSON Response
		ExtentManager.logStep("Validating response body for Email id");
		String email = APIUtility.getJSONValue(response, "email");
		boolean isEmailValid = "Sincere@april.biz".equals(email);
		softAssert.assertTrue(isEmailValid, "Email is NOT valid");
		if (isEmailValid) {
			ExtentManager.logStepValidationForAPI("Email Validation Passed!");
		} else {
			ExtentManager.logFailureAPI("Email Validation Failed");
		}

		softAssert.assertAll();
	}
}

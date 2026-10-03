package com.demoproject.utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class APIUtility {

	//Method to send the GET request
	public static Response sendGETRequest(String endpoint)
	{
		return RestAssured.get(endpoint);
	}
	
	//Method to send the POST request
	public static Response sendPOSTRequest(String endpoint, String payload)
	{
		return RestAssured.given().header("Content-Type","application/json")
		                   .body(payload)
		                   .post();
	}
	
	//Method to validate response status code
	public static boolean validateResponseCode(Response response, int expectedStatusCode)
	{
		return response.getStatusCode() == expectedStatusCode;
	}
	
	//Method to extract value from JSON response
	public static String getJSONValue(Response response, String value)
	{
		return response.jsonPath().getString(value);
	}
}

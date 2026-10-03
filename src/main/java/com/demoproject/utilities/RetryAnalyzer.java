package com.demoproject.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

	private int retryCount = 0; //Number of retries
	private static final int maxRetryCount = 2; //maximum number of retries
	@Override
	public boolean retry(ITestResult result) {
		if(retryCount<maxRetryCount)
		{
			retryCount++;
			return true; //retry the test
		}
		return false;
	}

}

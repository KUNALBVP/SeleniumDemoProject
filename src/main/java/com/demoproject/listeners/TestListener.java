package com.demoproject.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.demoproject.base.BaseClass;
import com.demoproject.utilities.ExtentManager;
import com.demoproject.utilities.RetryAnalyzer;

public class TestListener implements ITestListener, IAnnotationTransformer{

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}

	//trigger when a test start
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		//start logging in extent report
		ExtentManager.startTest(testName);
		ExtentManager.logStep("Test Started: "+testName);
	}

	//triggered when a test succeeds
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		if(!result.getTestClass().getName().toLowerCase().contains("api"))
		{
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test passed successfully", "Test End: "+ testName + "- PASSED");
		}
		else
		{
			ExtentManager.logStepValidationForAPI("Test End: "+ testName + "- PASSED");
		}
		
	}

	//triggered when a test fails
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String failutreMessage = result.getThrowable().getMessage();
		ExtentManager.logStep(failutreMessage);
		
		if(!result.getTestClass().getName().toLowerCase().contains("api"))
		{
			ExtentManager.logFailure(BaseClass.getDriver(), "Test failed", "Test End: "+ testName + "- FAILED");
		}
		else
		{
			ExtentManager.logFailureAPI("Test End: "+ testName + "- FAILED");
		}
		
	}

	//triggered when a test skips
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkip("Test skipped: "+testName);
	}

	//trigger when suite starts
	@Override
	public void onStart(ITestContext context) {
		//initialize extent report
		ExtentManager.getReporter();
	}

	//trigger when suite ends
	@Override
	public void onFinish(ITestContext context) {
		//Close the extent report
		ExtentManager.endTest();
	}

}

package com.demoproject.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
	
	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static Map<Long,WebDriver> driverMap = new HashMap<>();
	
	//Initialize Extent Report
	public synchronized static ExtentReports getReporter()
	{
		if(extent==null)
		{
			String reportPath = System.getProperty("user.dir")+"/src/test/resources/ExtentReport/ExtentReport.html";
			ExtentSparkReporter spark =new ExtentSparkReporter(reportPath);
			spark.config().setReportName("Automation Test Report");
			spark.config().setDocumentTitle("OrangeHRM");
			spark.config().setTheme(Theme.DARK);
			
			extent = new ExtentReports();
			extent.attachReporter(spark);
			//Adding system information
			extent.setSystemInfo("Operating System", System.getProperty("os.name"));
			extent.setSystemInfo("Java Version", System.getProperty("java.version"));
			extent.setSystemInfo("User Name", System.getProperty("user.name"));
		}
		return extent;
	}
	
	//Start the Test
	public synchronized static ExtentTest startTest(String testName)
	{
		ExtentTest extentTest = getReporter().createTest(testName);
		test.set(extentTest);
		return extentTest;
	}
	
	//End the Test
	public synchronized static void endTest()
	{
		getReporter().flush();
	}
	
	//Get Current thread's test name
	public synchronized static ExtentTest getTest()
	{
		return test.get();
	}
	
	//Method to get the name of the current test
	public static String getTestName()
	{
		ExtentTest currentTest = getTest();
		if(currentTest!=null)
		{
			return currentTest.getModel().getName();
		}
		else
		{
			return "No test is currently active for this thread";
		}
	}
	
	//Log a step
	public static void logStep(String logMessage)
	{
		getTest().info(logMessage);
	}
	
	//Log a step validation with Screenshot
	public static void logStepWithScreenshot(WebDriver driver, String logMessage, String screenshotMessage)
	{
		getTest().pass(logMessage);
		//Screenshot method
		attachScreenshot(driver, screenshotMessage);
		
	}
	
	
	//Log a step validation for API
		public static void logStepValidationForAPI(String logMessage)
		{
			getTest().pass(logMessage);	
		}
	
	
	//Log a failure
	public static void logFailure(WebDriver driver, String logMessage, String screenshotMessage)
	{
		String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
		getTest().fail(colorMessage);
		//Screenshot method
		attachScreenshot(driver, screenshotMessage);
	}
	
	
	//Log a failure for API
		public static void logFailureAPI(String logMessage)
		{
			String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
			getTest().fail(colorMessage);
		}
	
	
	//Log a skip test
	public static void logSkip(String logMessage)
	{
		String colorMessage = String.format("<span style='color:orange;'>%s</span>", logMessage);
		getTest().skip(colorMessage);
	}
	
	//Take the Screenshot with date and time in the file
	public synchronized static String takeScreenshot(WebDriver driver, String screenshotName)
	{
		TakesScreenshot ts = (TakesScreenshot)driver;
		File src = ts.getScreenshotAs(OutputType.FILE);
		//format date and time for file name
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		
		//Saving the screenshot to the file
		String destPath = System.getProperty("user.dir")+"/src/test/resources/screenshots/"+screenshotName+"_"+timeStamp+".png";
		File finalPath = new File(destPath);
		try {
			FileUtils.copyFile(src, finalPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Convert screenshot to Base64 for embedding in the report
		String base64Format = convertToBase64(src);
		return base64Format;
	}
	
	//Convert screenshot to Base64 format
	public static String convertToBase64(File screenshotFile)
	{
		String base64Format = "";
		//Read the file content into Byte Array
		try {
			byte[] fileContent = FileUtils.readFileToByteArray(screenshotFile);
			base64Format = Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return base64Format;
		
	}
	
	//Attach screenshot to report using Base64
	public synchronized static void attachScreenshot(WebDriver driver, String message)
	{
		try {
			String screenshotBase64 = takeScreenshot(driver, getTestName());
			getTest().info(message, com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(screenshotBase64).build());
		} catch (Exception e) {
			getTest().fail("Failed to attach screenshot"+message);
			e.printStackTrace();
		}
		
	}
	
	//Register webdriver for current thread
	public static void registerDriver(WebDriver driver)
	{
		driverMap.put(Thread.currentThread().getId(), driver);
	}

}

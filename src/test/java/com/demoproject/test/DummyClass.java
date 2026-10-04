package com.demoproject.test;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.demoproject.base.BaseClass;
import com.demoproject.utilities.ExtentManager;

public class DummyClass extends BaseClass {
	
	@Test
	public void dummyTest()
	{
		//Test Checkin
		//ExtentManager.startTest("DummyTest1 Test");  --this has been implemented in TestListener
		String title = getDriver().getTitle();
		ExtentManager.logStep("Verifying the title");
		assert title.equals("OrangeHRM"):"Test failed - Title is NOT matching";
		
		System.out.println("Test passed - Title is matching");
		//ExtentManager.logSkip("Skipping the Test as part of testing");
		throw new SkipException("Skipping the test as part of testing");
	}

}

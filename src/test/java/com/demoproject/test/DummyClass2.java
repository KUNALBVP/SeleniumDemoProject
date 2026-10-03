package com.demoproject.test;

import org.testng.annotations.Test;

import com.demoproject.base.BaseClass;
import com.demoproject.utilities.ExtentManager;

public class DummyClass2 extends BaseClass {
	
	@Test
	public void dummyTest2()
	{
		//ExtentManager.startTest("DummyTest2 Test");  --this has been implemented in TestListener
		String title = getDriver().getTitle();
		ExtentManager.logStep("Verifying the title");
		assert title.equals("OrangeHRM"):"Test failed - Title is NOT matching";
		
		System.out.println("Test passed - Title is matching");
		ExtentManager.logStep("Validation Successful");
		
	}

}

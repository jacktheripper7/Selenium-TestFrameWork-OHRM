package com.orangehrm.test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class SampleTest extends BaseClass {
    @Test
    public void sampleTest() {
        //Adding a comment to test the git
        ExtentManager.logStep("Navigating to OrangeHRM");
        String title = getDriver().getTitle();
        ExtentManager.logStep("Verifying the title of the page");
        assertEquals("OrangeHRM", title);
        System.out.println("Title is matched");
    }
}

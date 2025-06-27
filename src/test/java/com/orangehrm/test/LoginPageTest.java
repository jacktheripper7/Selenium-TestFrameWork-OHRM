package com.orangehrm.test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginPageTest extends BaseClass {
    private LoginPage loginPage;
    private HomePage homePage;

    //method to set up the page objects
    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
    }


    @Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class)
    public void verifyValidPerformLogin(String username, String password) {
        ExtentManager.logStep("Performing valid login by providing valid credentials");
        loginPage.performLogin(username, password);
        boolean isUniqueAdminTabDisplayed = homePage.isUniqueAdminTabDisplayed();
        boolean isLogoDisplayed = homePage.isLogoDisplayed();
        ExtentManager.logStep("Verifying if admin tab and logo are displayed");
        Assert.assertTrue(isUniqueAdminTabDisplayed, "Admin tab is not displayed");
        Assert.assertTrue(isLogoDisplayed, "Logo is not displayed");
        ExtentManager.logStep("Valid login is successful");
        homePage.logout();
        ExtentManager.logStep("Logout is successful");

        //Add wait for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(dataProvider = "inValidLoginData", dataProviderClass = DataProviders.class)
    public void verifyInvalidPerformLogin(String username, String password) {
        ExtentManager.logStep("Performing invalid login by providing invalid credentials");
        loginPage.performLogin(username, password);
        boolean isErrorMessageDisplayed = loginPage.isErrorMessageDisplayed();
        Assert.assertTrue(isErrorMessageDisplayed, "Error message is not displayed");
        Assert.assertTrue(loginPage.assertErrorMessage("Invalid credentials"), "Invalid login is not successful");
        ExtentManager.logStep("Invalid login is successful");
    }
}

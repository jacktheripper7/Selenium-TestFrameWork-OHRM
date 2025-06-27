package com.orangehrm.test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class HomePageTest extends BaseClass {
    private LoginPage loginPage;
    private HomePage homePage;

    //method to set up the page objects
    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
    }

    @Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class)
    public void verifyLogoIsDisplayed(String username, String password) {
        loginPage.performLogin(username, password);
        boolean isLogoDisplayed = homePage.isLogoDisplayed();
        Assert.assertTrue(isLogoDisplayed, "Logo is not displayed");
    }

}

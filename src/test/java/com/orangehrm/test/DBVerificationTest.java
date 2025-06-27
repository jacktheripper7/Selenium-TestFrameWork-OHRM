package com.orangehrm.test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DBConnection;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class DBVerificationTest extends BaseClass {
    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
    }


    @Test(dataProvider = "empVerification", dataProviderClass = DataProviders .class)
    public void verifyEmployeeDetails(String empId, String empName) {
        ExtentManager.logStep("Performing valid login by providing valid credentials");
        loginPage.performLogin(properties.getProperty("username"), properties.getProperty("password"));

        ExtentManager.logStep("Navigating to PIM tab");
        homePage.navigateToPIMTab();

        ExtentManager.logStep("Search for employee");
        homePage.searchEmployee(empName);

        ExtentManager.logStep("Get employee details from DB");
        //Fetch employee details from DB and store in a map
        Map<String, String> employeeDetailsFromDB = DBConnection.getEmployeeDetails(empId);


        String firstName = employeeDetailsFromDB.get("firstName");
        String lastName = employeeDetailsFromDB.get("lastName");
        String middleName = employeeDetailsFromDB.get("middleName");

        String empFirstAndMiddleName = (firstName + " " + middleName).trim();

        ExtentManager.logStep("Verifying the first name and middle name of the employee from UI and DB");
        Assert.assertTrue(homePage.verifyEmployeeFirstAndMiddleName(empFirstAndMiddleName), "Employee first name and middle name does not match");

        ExtentManager.logStep("Verifying the last name of the employee from UI and DB");
        Assert.assertTrue(homePage.verifyEmployeeLastName(lastName), "Employee last name does not match");

        ExtentManager.logStep("Data verification is successful");

        ExtentManager.logStep("Logout is successful");
        homePage.logout();

        ExtentManager.logStep("Logout is successful");
    }
}

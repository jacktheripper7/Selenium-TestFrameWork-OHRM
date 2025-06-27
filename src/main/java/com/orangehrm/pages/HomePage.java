package com.orangehrm.pages;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {
    private final ActionDriver actionDriver;

    //Define Locators using By class with CSS selectors
    private final By uniqueAdminTab = By.xpath("//ul[@class='oxd-main-menu']//span[text()='Admin']");
    private final By userIdButton = By.className("oxd-userdropdown-name");
    private final By logoutButton = By.xpath("//a[text()='Logout']");
    private final By logo = By.xpath("//a[@href='https://www.orangehrm.com/']");
    private final By PIMTab = By.xpath("//ul[@class='oxd-main-menu']//span[text()='PIM']");
    private final By employeeSearch = By.xpath("//label[text()='Employee Name']/parent::div/following-sibling::div/div/div/input");
    private final By searchButton = By.cssSelector("button[type='submit']");
    private final By employeeFirstAndMiddleName = By.xpath("//div[@class='oxd-table-card']/div/div[3]");
    private final By employeeLastName = By.xpath("//div[@class='oxd-table-card']/div/div[4]");

    public HomePage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

    //Mehtod to verify admin tab is displayed
    public boolean isUniqueAdminTabDisplayed() {
        return actionDriver.isElementDisplayed(uniqueAdminTab);
    }

    //Method to verify logo is displayed
    public boolean isLogoDisplayed() {
        return actionDriver.isElementDisplayed(logo);
    }

    //Method to perform logout
    public void logout() {
        actionDriver.clickElement(userIdButton);
        actionDriver.clickElement(logoutButton);
    }

    //Method to navigate to PIM tab
    public void navigateToPIMTab() {
        actionDriver.clickElement(PIMTab);
    }

    //Method to search for an employee
    public void searchEmployee(String employeeName) {
        actionDriver.enterText(employeeSearch, employeeName);
        actionDriver.clickElement(searchButton);
        actionDriver.scrollToElement(employeeFirstAndMiddleName);
        //Adding a delay to wait for the employee details to load
        actionDriver.staticWaitInSeconds(1);

    }

    //Verify employee first and middle name is displayed
    public boolean verifyEmployeeFirstAndMiddleName(String empFirstAndMiddleNameFromDB) {
         return actionDriver.compareText(employeeFirstAndMiddleName, empFirstAndMiddleNameFromDB);
    }

    //Verify employee last name is displayed
    public boolean verifyEmployeeLastName(String empLastNameFromDB) {
        return actionDriver.compareText(employeeLastName, empLastNameFromDB);
    }
}

package com.orangehrm.pages;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.testng.Assert.assertTrue;

public class LoginPage {
    private final ActionDriver actionDriver;

    //Define Locators using By class
    private final By usernameField = By.name("username");
    private final By passwordField = By.cssSelector("input[type='password']");
    private final By loginButtonField = By.xpath("//button[text()=' Login ']");
    private final By errorMessageField = By.xpath("//p[contains(@class, 'oxd-alert-content-text')]");

    //Constructor
    public LoginPage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

    //Method to perform login
    public void performLogin(String username, String password) {
        actionDriver.enterText(usernameField, username);
        actionDriver.enterText(passwordField, password);
        actionDriver.clickElement(loginButtonField);
    }

    //Method to check error message
    public boolean isErrorMessageDisplayed() {
         return actionDriver.isElementDisplayed(errorMessageField);
    }

    //Method to get error message
    public String getErrorMessageText() {
        return actionDriver.getText(errorMessageField);
    }

    // Method to assert error message
    public boolean assertErrorMessage(String expectedMessage) {
        return actionDriver.compareText(errorMessageField, expectedMessage);
    }
}

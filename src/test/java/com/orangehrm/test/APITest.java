package com.orangehrm.test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.APIUtility;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.Test;

public class APITest {
    @Test
    public void verifyGetUserAPI() {
        //Step 1 : Define the endpoint
        String endpoint = "https://jsonplaceholder.typicode.com/users/1";
        ExtentManager.logStep("Performing a GET request on the endpoint: " + endpoint);

        //Step 2 : Send the GET request
        ExtentManager.logStep("Sending the GET request");
        Response response = APIUtility.sendGetRequest(endpoint);

        //Step 3 : Validate the response status code
        ExtentManager.logStep("Validating the response status code");
        boolean isStatusCodeValid = APIUtility.validateResponseStatusCode(response, 200);

        if (isStatusCodeValid)
            ExtentManager.logPassAPI("Response status code is 200");
        else
            ExtentManager.logFailureAPI("Response status code is not 200");

        Assert.assertTrue(isStatusCodeValid, "Response status code is not 200");

        //Step 4 : Extract the JSON response value username and email
        ExtentManager.logStep("Extracting the JSON response value");
        String name = APIUtility.extractJsonResponse(response, "username");
        String email = APIUtility.extractJsonResponse(response, "email");
        boolean isNameValid = name.equals("Bret");
        boolean isEmailValid = email.equals("Sincere@april.biz");

        if (isNameValid && isEmailValid) {
            ExtentManager.logPassAPI("Username and email are valid");
        } else {
            ExtentManager.logFailureAPI("Username and email are not valid");
        }
        Assert.assertTrue(isNameValid && isEmailValid, "Username and email are not valid");
    }
}

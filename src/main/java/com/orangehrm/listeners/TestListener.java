package com.orangehrm.listeners;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;
import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestListener implements ITestListener, IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        //Starts logging in Extent Report
        ExtentManager.startTest(testName);
        ExtentManager.logStep("Test started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        //Trigger when test is passed
        String testName = result.getMethod().getMethodName();

        if (result.getTestClass().getName().toLowerCase().contains("api")) {
            ExtentManager.logPassAPI("Test End: " + testName + " - ✔ Test Passed");
        }
        else
            ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test passed successfully!", "Test End: " + testName + " - ✔ Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        //Trigger when test is failed
        String testName = result.getMethod().getMethodName();
        String failure = result.getThrowable().getMessage();
        if (result.getTestClass().getName().toLowerCase().contains("api")) {
            ExtentManager.logFailureAPI(failure);
            ExtentManager.logStep("Test End: " + testName + " - ✘ Test Failed");
        } else {
            ExtentManager.logStep(failure);
            ExtentManager.logFailure(BaseClass.getDriver(), "Test failed!", "Test End: " + testName + " - ✘ Test Failed");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String skip = result.getThrowable().getMessage();
        ExtentManager.logStep(skip);
        ExtentManager.logSkip("Test skipped: " + testName + " - ✘ Test Skipped");
    }

    //This method is called when the test suite is about to start
    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getReporter();
    }

    //This method is called when the test suite is about to end
    @Override
    public void onFinish(ITestContext context) {
        //Flush the report
        ExtentManager.endTest();
    }
}

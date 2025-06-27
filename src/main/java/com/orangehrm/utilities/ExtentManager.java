package com.orangehrm.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.orangehrm.base.BaseClass;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtentManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final Map<Long, WebDriver> driverMap = new HashMap<>();
    private static Logger logger = BaseClass.logger;

    public synchronized static ExtentReports getReporter() {
        if (extent == null) {
            String reportPath = System.getProperty("user.dir") + "/src/test/resources/ExtentReport/ExtentReport.html";
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setDocumentTitle("OrangeHRM Automation Test Report");
            spark.config().setReportName("OrangeHRM Automation Report");
            spark.config().setTheme(Theme.DARK);
            extent = new ExtentReports();
            extent.attachReporter(spark);
            // Adding system info
            extent.setSystemInfo("Environment", "QA");
//            extent.setSystemInfo("Browser", BaseClass.getProperties().getProperty("browser"));
            extent.setSystemInfo("Java version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));

        }
        return extent;
    }

    //Start the test
    public synchronized static ExtentTest startTest(String testName) {
        ExtentTest testName1 = getReporter().createTest(testName);
        extentTest.set(testName1);
        return testName1;
    }

    //End the test
    public synchronized static void endTest() {
        getReporter().flush();
    }

    //Get current thread's test
    public synchronized static ExtentTest getTest() {
        return extentTest.get();
    }

    //Get the test name of the current test
    public static String getTestName() {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            return currentTest.getModel().getName();
        }
        else {
            return "No test is currently running for this thread";
        }
    }

    //Log a step
    public static void logStep(String step) {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.info(step);
        }
        else {
            System.out.println("No test is currently running for this thread");
        }
    }

    //Log a step with screenshot
    public static void logStepWithScreenshot(WebDriver driver, String step, String screenShotMessage) {
        getTest().pass(step);
        //Screenshot method
        attachScreenshot(driver, screenShotMessage);
    }

    //Log a failure
    public static void logFailure(WebDriver driver,String screenShotMessage , String failure) {
        String colorMessage = String.format("<span style=\"color:red\">%s</span>", failure);
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.fail(colorMessage);
            attachScreenshot(driver, screenShotMessage);

        }
        else {
            System.out.println("No test is currently running for this thread");
        }
    }

    //Log a failure for API
    public static void logFailureAPI(String failure) {
        String colorMessage = String.format("<span style=\"color:red\">%s</span>", failure);
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.fail(colorMessage);
        }
        else {
           logger.info("No test is currently running for this thread");
        }
    }

    //Log a pass for API
    public static void logPassAPI(String step) {
        String colorMessage = String.format("<span style=\"color:green\">%s</span>", step);
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.pass(colorMessage);
        }
        else {
            logger.info("No test is currently running for this thread");
        }
    }

    //Log a skip
    public static void logSkip(String skip) {
        String colorMessage = String.format("<span style=\"color:orange\">%s</span>", skip);
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            currentTest.skip(colorMessage);
        }
        else {
            System.out.println("No test is currently running for this thread");
        }
    }

    //Take a screenshot with date and time
    public synchronized static String takeScreenshot(WebDriver driver, String screenshotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);

        //Format date and time for file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        //Saving screenshot to folder
        String screenshotPath = System.getProperty("user.dir") + "/src/test/resources/screenshots/" + screenshotName + "_" + timeStamp + ".png";

        File finalPath = new File(screenshotPath);
        try {
            FileUtils.copyFile(src, finalPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return convertScreenshotToBase64(finalPath);
    }

    //Convert screenshot to base64 format
    public static String convertScreenshotToBase64(File screenshotFile) {
        String base64format = "";
        //Read the file content into a byte array
        byte[] fileContent = null;
        try {
            fileContent = FileUtils.readFileToByteArray(screenshotFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        base64format = "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);
        return base64format;
    }

    //Attach screenshot to report using Base64
    public synchronized static void attachScreenshot(WebDriver driver, String message) {
        try {
            String screenshotBase64 = takeScreenshot(driver, getTestName());
            getTest().info(message, MediaEntityBuilder.createScreenCaptureFromBase64String(screenshotBase64).build());
        } catch (Exception e) {
            getTest().fail("Failed to attach screenshot: " + message);
            throw new RuntimeException(e);
        }
    }

    public static void registerDriver(WebDriver driver) {
        driverMap.put(Thread.currentThread().threadId(), driver);
    }
}

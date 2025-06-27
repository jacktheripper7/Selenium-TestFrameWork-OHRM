package com.orangehrm.base;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Properties;


import static java.lang.Integer.parseInt;

public class BaseClass {
    protected static Properties properties;
    protected static WebDriver driver;
    private static ActionDriver actionDriver;
    public static final Logger logger = LoggerManager.logger(BaseClass.class);

    private static ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<ActionDriver> actionDriverThreadLocal = new ThreadLocal<>();

    @BeforeSuite
    public void loadConfig() throws IOException {
        properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config.properties");
        properties.load(fileInputStream);
        logger.info("Config file loaded");
    }


    @BeforeMethod
    public synchronized void setup(Method method) {
        System.out.println("Setting up WebDriver for:" + this.getClass().getSimpleName());
        launchBrowser();
        configureBrowser();
        // Initialize ActionDriver only once
//        if (actionDriver == null) {
//            actionDriver = new ActionDriver(driver);
//        }

        // Initialize ActionDriver using ThreadLocal
        actionDriverThreadLocal.set(new ActionDriver(getDriver()));
        logger.info("ActionDriver initialized for thread: {}", Thread.currentThread().threadId());
    }


    private synchronized void launchBrowser() {
        String browser = properties.getProperty("browser");
        if (browser.equalsIgnoreCase("chrome")) {
//            driver = new ChromeDriver();
            // Create ChromeOptions
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // Run Chrome in headless mode
            options.addArguments("--disable-gpu"); // Disable GPU for headless mode
//            options.addArguments ("--window-size=1920,1080"); // Set window size
            options.addArguments("--disable-notifications"); // Disable browser notifications
            options.addArguments("--no-sandbox"); // Required for some CI environments li
            options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resources
            webDriverThreadLocal.set(new ChromeDriver());
            ExtentManager.registerDriver(getDriver());
            logger.info("Chrome browser launched");
        } else if (browser.equalsIgnoreCase("firefox")) {
//            driver = new FirefoxDriver();
            //Create FirefoxOptions
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless"); // Run Firefox in headless mode
            options.addArguments("--disable-gpu"); // Disable GPU for headless mode
            options.addArguments("--width=1920"); // Set window width
            options.addArguments("--height=1080"); // Set window height
            options.addArguments("--disable-notifications"); // Disable browser notifications
            options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
            options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resources
            webDriverThreadLocal.set(new FirefoxDriver());
            ExtentManager.registerDriver(getDriver());
            logger.info("Firefox browser launched");
        } else if (browser.equalsIgnoreCase("edge")) {
//            driver = new EdgeDriver();
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--headless"); // Run Edge in headless mode
            options.addArguments("--disable-gpu"); // Disable GPU for headless mode
            options.addArguments("--window-size=1920,1080"); // Set window size
            options.addArguments("--disable-notifications"); // Disable browser notifications
            options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
            options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resources
            webDriverThreadLocal.set(new EdgeDriver());
            ExtentManager.registerDriver(getDriver());
            logger.info("Edge browser launched");
        } else {
            throw new IllegalArgumentException("Invalid browser type: " + browser);
        }

    }

    //Configure browser settings such as implicit wait and maximize window
    private void configureBrowser() {
        // Implicit Wait
        int implicitWait = parseInt(properties.getProperty("implicitWait"));
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

        //Maximize the window
        getDriver().manage().window().maximize();

        //Navigate to OrangeHRM
        try {
            getDriver().get(properties.getProperty("url"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to OrangeHRM", e);
        }
    }


    @AfterMethod
    public synchronized void tearDown() {
        if (getDriver() != null)
            try {
                getDriver().quit();
            } catch (Exception e) {
                throw new RuntimeException("Failed to quit the driver", e);
            }
        logger.info("WebDriver closed");
//        driver = null;
//        actionDriver = null;
        actionDriverThreadLocal.remove();
        webDriverThreadLocal.remove();
    }

//    // Driver getter method
//    public static WebDriver getDriver() {
//        return driver;
//    }

//    //Getter method for ActionDriver
//    public static WebDriver getDriver() {
//        if (driver == null) {
//            throw new IllegalStateException("The driver has not been initialized yet.");
//        }
//        return driver;
//    }

    // Getter method for WebDriver - Thread Safe
    public static WebDriver getDriver() {
        if (webDriverThreadLocal.get() == null) {
            throw new IllegalStateException("The driver has not been initialized yet.");
        }
        return webDriverThreadLocal.get();
    }

    //Getter method for ActionDriver
//    public static ActionDriver getActionDriver() {
//        if (actionDriver == null) {
//            throw new IllegalStateException("The ActionDriver has not been initialized yet.");
//        }
//        return actionDriver;
//    }

    // Getter method for ActionDriver - Thread Safe
    public static ActionDriver getActionDriver() {
        if (actionDriverThreadLocal.get() == null) {
            throw new IllegalStateException("The ActionDriver has not been initialized yet.");
        }
        return actionDriverThreadLocal.get();
    }

    // Driver setter method
    public void setDriver(WebDriver driver) {
        BaseClass.driver = driver;
    }

    public static Properties getProperties() {
        return properties;
    }
}

package com.orangehrm.actiondriver;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.orangehrm.base.BaseClass.getDriver;

public class ActionDriver {

    private final WebDriver driver;
    private final WebDriverWait wait;
    public static final Logger logger = BaseClass.logger;

    public ActionDriver(WebDriver driver) {
        this.driver = driver;
        int explicitWait = Integer.parseInt(BaseClass.getProperties().getProperty("explicitWait"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        logger.info("ActionDriver / WebDriver initialized");
    }

    //Wait for Element to be clickable
    public void waitForElementToBeClickable(By by) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            throw new RuntimeException("Element is not clickable", e);
        }
    }

    // Method to click an element
    public void clickElement(By by) {
        String elementDescription = getElementDescription(by);
        try {
            waitForElementToBeClickable(by);
            applyBorder(by, "green");
            driver.findElement(by).click();
            ExtentManager.logStep("Clicked an element: " + elementDescription);
            logger.info("Clicked element: {}", elementDescription);
        } catch (Exception e) {
            ExtentManager.logFailure(getDriver(), "Unable to click an element:", "Unable to click element: " + elementDescription);
            applyBorder(by, "red");
            throw new RuntimeException("Unable to click element", e);
        }
    }

    //Method to Enter text
    public void enterText(By by, String text) {
        String elementDescription = getElementDescription(by);
        waitForElementToBeVisible(by);
        driver.findElement(by).clear();
        try {
            applyBorder(by, "green");
            logger.info("Entered text '{}' in element: {}", text, elementDescription);
            driver.findElement(by).sendKeys(text);
        } catch (Exception e) {
            applyBorder(by, "red");
            ExtentManager.logFailure(getDriver(), "Unable to enter text:", "Unable to enter text in element: " + elementDescription);
            throw new RuntimeException("Unable to enter text", e);
        }
    }

    /**
     * Wait for an element to be visible.
     *
     * @param by The by which to find the element
     */
    private void waitForElementToBeVisible(By by) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            throw new RuntimeException("Element is not visible", e);
        }
    }

    //Method to get text
    public String getText(By by) {
        waitForElementToBeVisible(by);
        try {
            return driver.findElement(by).getText();
        } catch (Exception e) {
            throw new RuntimeException("Unable to get text", e);
        }
    }

    // Method to compare text
    public boolean compareText(By by, String expectedText) {
        try {
            waitForElementToBeVisible(by);
            String actualText = driver.findElement(by).getText();
            if (actualText.equals(expectedText)) {
                applyBorder(by, "green");
                logger.info("Text matched: {}", expectedText);
                ExtentManager.logStepWithScreenshot(getDriver(), "Text matched: " + expectedText, "Text matched: " + expectedText);
                return true;
            } else {
                applyBorder(by, "red");
                logger.error("Text did not match: {} not equals to {}", actualText, expectedText);
                ExtentManager.logFailure(getDriver(), "Text comparison failed:", "Text did not match: " + actualText + " not equals to " + expectedText);
                return false;
            }
        } catch (Exception e) {
            applyBorder(by, "red");
            throw new RuntimeException("Unable to compare text", e);
        }
    }

    // Method to check if element is displayed
    public boolean isElementDisplayed(By by) {
        waitForElementToBeVisible(by);
        try {
            applyBorder(by, "green");
            logger.info("Element is displayed: {}", getElementDescription(by));
            ExtentManager.logStep("Element is displayed: " + getElementDescription(by));
            ExtentManager.logStepWithScreenshot(getDriver(), "Element is displayed: " + getElementDescription(by), "Element is displayed: " + getElementDescription(by));
            return driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            applyBorder(by, "red");
            ExtentManager.logFailure(getDriver(), "Element is not displayed:", "Element is not displayed: " + getElementDescription(by));
            throw new RuntimeException("Unable to check if element is displayed", e);
        }
    }
    // Method to scroll to an element
    public void scrollToElement(By by) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement element = driver.findElement(by);
            js.executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate and scroll to element", e);
        }
    }

    //Waiting for page to load
    public void waitForPageToLoad(int timeoutInSeconds) {
        try {
            wait.withTimeout(Duration.ofSeconds(timeoutInSeconds)).until(WebDriver -> {
            String readyState = (String) ((JavascriptExecutor) WebDriver).executeScript("return document.readyState");
            return "complete".equals(readyState);
        });
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        } catch (Exception e) {
            throw new RuntimeException("Unable to wait for page to load", e);
        }
    }

    //Method to get description of element by its By locator
    public String getElementDescription(By by) {

        if (driver == null) {
            throw new RuntimeException("Driver is null");
        }
        if (by == null) {
            throw new RuntimeException("By locator is null");
        }

        try {
            WebElement element = driver.findElement(by);
            String name = element.getDomAttribute("name");
            String id = element.getDomAttribute("id");
            String className = element.getDomAttribute("class");
            String tagName = element.getTagName();
            String placeholder = element.getDomAttribute("placeholder");
            String text = element.getText();

            if (isNotEmpty(name)) {
                return "Element with name: " + name;
            } else if (isNotEmpty(id)) {
                return "Element with id: " + id;
            } else if (isNotEmpty(className)) {
                return "Element with class: " + className;
            } else if (isNotEmpty(tagName)) {
                return "Element with tag name: " + tagName;
            } else if (isNotEmpty(placeholder)) {
                return "Element with placeholder: " + placeholder;
            } else if (isNotEmpty(text)) {
                return "Element with text: " + truncateText(text, 50);
            }
        } catch (Exception e) {
            logger.error("Unable to get element description: {}", e.getMessage());
        }
        return "Unable to get element description";
    }

    public boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }

    //Utility method to truncate long text
    public String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    //Utility method to border an element
    public void applyBorder(By by, String color) {
        try {
            WebElement element = driver.findElement(by);
            //Apply border to the element
            String script = "arguments[0].style.border = '3px solid " + color + "';";
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(script, element);
            logger.info("Border applied with color {} to element: {}", color, getElementDescription(by));
            ExtentManager.logStep("Border applied to element: " + getElementDescription(by));
        } catch (Exception e) {
            logger.warn("Failed to apply border to element: {}", getElementDescription(by));
            throw new RuntimeException("Unable to apply border to element", e);
        }
    }

    public void staticWaitInSeconds(int i) {
        try {
            Thread.sleep(i * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // ===================== Select Methods =====================

    // Method to select a dropdown by visible text
    public void selectByVisibleText(By by, String value) {
        try {
            WebElement element = driver.findElement(by);
            new Select(element).selectByVisibleText(value);
            applyBorder(by, "green");
            logger.info("Selected dropdown value: " + value);
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to select dropdown value: " + value, e);
        }
    }

    // Method to select a dropdown by value
    public void selectByValue(By by, String value) {
        try {
            WebElement element = driver.findElement(by);
            new Select(element).selectByValue(value);
            applyBorder(by, "green");
            logger.info("Selected dropdown value by actual value: {}", value);
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to select dropdown by value: {}", value, e);
        }
    }

    // Method to select a dropdown by index
    public void selectByIndex(By by, int index) {
        try {
            WebElement element = driver.findElement(by);
            new Select(element).selectByIndex(index);
            applyBorder(by, "green");
            logger.info("Selected dropdown value by index: " + index);
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to select dropdown by index: " + index, e);
        }
    }

    // Method to get all options from a dropdown
    public List<String> getDropdownOptions(By by) {
        List<String> optionsList = new ArrayList<>();
        try {
            WebElement dropdownElement = driver.findElement(by);
            Select select = new Select(dropdownElement);
            for (WebElement option : select.getOptions()) {
                optionsList.add(option.getText());
            }
            applyBorder(by, "green");
            logger.info("Retrieved dropdown options for " + getElementDescription(by));
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to get dropdown options: " + e.getMessage());
        }
        return optionsList;
    }


    // ===================== JavaScript Utility Methods =====================

    // Method to click using JavaScript
    public void clickUsingJS(By by) {
        try {
            WebElement element = driver.findElement(by);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            applyBorder(by, "green");
            logger.info("Clicked element using JavaScript: " + getElementDescription(by));
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to click using JavaScript", e);
        }
    }

    // Method to scroll to the bottom of the page
    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.info("Scrolled to the bottom of the page.");
    }

    // Method to highlight an element using JavaScript
    public void highlightElementJS(By by) {
        try {
            WebElement element = driver.findElement(by);
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid yellow'", element);
            logger.info("Highlighted element using JavaScript: " + getElementDescription(by));
        } catch (Exception e) {
            logger.error("Unable to highlight element using JavaScript", e);
        }
    }

    // ===================== Window and Frame Handling =====================

    // Method to switch between browser windows
    public void switchToWindow(String windowTitle) {
        try {
            Set<String> windows = driver.getWindowHandles();
            for (String window : windows) {
                driver.switchTo().window(window);
                if (driver.getTitle().equals(windowTitle)) {
                    logger.info("Switched to window: " + windowTitle);
                    return;
                }
            }
            logger.warn("Window with title " + windowTitle + " not found.");
        } catch (Exception e) {
            logger.error("Unable to switch window", e);
        }
    }

    // Method to switch to an iframe
    public void switchToFrame(By by) {
        try {
            driver.switchTo().frame(driver.findElement(by));
            logger.info("Switched to iframe: " + getElementDescription(by));
        } catch (Exception e) {
            logger.error("Unable to switch to iframe", e);
        }
    }

    // Method to switch back to the default content
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        logger.info("Switched back to default content.");
    }

    // ===================== Alert Handling =====================

    // Method to accept an alert popup
    public void acceptAlert() {
        try {
            driver.switchTo().alert().accept();
            logger.info("Alert accepted.");
        } catch (Exception e) {
            logger.error("No alert found to accept", e);
        }
    }

    // Method to dismiss an alert popup
    public void dismissAlert() {
        try {
            driver.switchTo().alert().dismiss();
            logger.info("Alert dismissed.");
        } catch (Exception e) {
            logger.error("No alert found to dismiss", e);
        }
    }

    // Method to get alert text
    public String getAlertText() {
        try {
            return driver.switchTo().alert().getText();
        } catch (Exception e) {
            logger.error("No alert text found", e);
            return "";
        }
    }

    // ===================== Browser Actions =====================

    public void refreshPage() {
        try {
            driver.navigate().refresh();
            ExtentManager.logStep("Page refreshed successfully.");
            logger.info("Page refreshed successfully.");
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to refresh page", "refresh_page_failed");
            logger.error("Unable to refresh page: " + e.getMessage());
        }
    }

    public String getCurrentURL() {
        try {
            String url = driver.getCurrentUrl();
            ExtentManager.logStep("Current URL fetched: " + url);
            logger.info("Current URL fetched: " + url);
            return url;
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to fetch current URL", "get_current_url_failed");
            logger.error("Unable to fetch current URL: " + e.getMessage());
            return null;
        }
    }

    public void maximizeWindow() {
        try {
            driver.manage().window().maximize();
            ExtentManager.logStep("Browser window maximized.");
            logger.info("Browser window maximized.");
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to maximize window", "maximize_window_failed");
            logger.error("Unable to maximize window: " + e.getMessage());
        }
    }

    // ===================== Advanced WebElement Actions =====================

    public void moveToElement(By by) {
        String elementDescription = getElementDescription(by);
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(driver.findElement(by)).perform();
            ExtentManager.logStep("Moved to element: " + elementDescription);
            logger.info("Moved to element --> " + elementDescription);
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to move to element", elementDescription + "_move_failed");
            logger.error("Unable to move to element: " + e.getMessage());
        }
    }

    public void dragAndDrop(By source, By target) {
        String sourceDescription = getElementDescription(source);
        String targetDescription = getElementDescription(target);
        try {
            Actions actions = new Actions(driver);
            actions.dragAndDrop(driver.findElement(source), driver.findElement(target)).perform();
            ExtentManager.logStep("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
            logger.info("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to drag and drop", sourceDescription + "_drag_failed");
            logger.error("Unable to drag and drop: " + e.getMessage());
        }
    }

    public void doubleClick(By by) {
        String elementDescription = getElementDescription(by);
        try {
            Actions actions = new Actions(driver);
            actions.doubleClick(driver.findElement(by)).perform();
            ExtentManager.logStep("Double-clicked on element: " + elementDescription);
            logger.info("Double-clicked on element --> " + elementDescription);
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to double-click element", elementDescription + "_doubleclick_failed");
            logger.error("Unable to double-click element: " + e.getMessage());
        }
    }

    public void rightClick(By by) {
        String elementDescription = getElementDescription(by);
        try {
            Actions actions = new Actions(driver);
            actions.contextClick(driver.findElement(by)).perform();
            ExtentManager.logStep("Right-clicked on element: " + elementDescription);
            logger.info("Right-clicked on element --> " + elementDescription);
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to right-click element", elementDescription + "_rightclick_failed");
            logger.error("Unable to right-click element: " + e.getMessage());
        }
    }

    public void sendKeysWithActions(By by, String value) {
        String elementDescription = getElementDescription(by);
        try {
            Actions actions = new Actions(driver);
            actions.sendKeys(driver.findElement(by), value).perform();
            ExtentManager.logStep("Sent keys to element: " + elementDescription + " | Value: " + value);
            logger.info("Sent keys to element --> " + elementDescription + " | Value: " + value);
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to send keys", elementDescription + "_sendkeys_failed");
            logger.error("Unable to send keys to element: " + e.getMessage());
        }
    }

    public void clearText(By by) {
        String elementDescription = getElementDescription(by);
        try {
            driver.findElement(by).clear();
            ExtentManager.logStep("Cleared text in element: " + elementDescription);
            logger.info("Cleared text in element --> " + elementDescription);
        } catch (Exception e) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Unable to clear text", elementDescription + "_clear_failed");
            logger.error("Unable to clear text in element: " + e.getMessage());
        }
    }

    // Method to upload a file
    public void uploadFile(By by, String filePath) {
        try {
            driver.findElement(by).sendKeys(filePath);
            applyBorder(by, "green");
            logger.info("Uploaded file: " + filePath);
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to upload file: " + e.getMessage());
        }
    }
}

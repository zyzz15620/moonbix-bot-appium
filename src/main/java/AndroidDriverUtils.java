import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AndroidDriverUtils {
    private static AndroidDriver driver;
    private static WebDriverWait webDriverWait;
    public static Map<String, Integer> middleScreenLocation;
    private static final Logger logger = DriverLogger.getLogger();

    public static AndroidDriver getAndroidDriver() {
        if (driver == null || driver.getSessionId() == null) {
            try {
                logger.info("Initializing new AndroidDriver...");
                DesiredCapabilities desiredCapabilities = getDesiredCapabilities();

                driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), desiredCapabilities);
                webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                middleScreenLocation = new HashMap<>();
                middleScreenLocation.put("x", driver.manage().window().getSize().width / 2);
                middleScreenLocation.put("y", driver.manage().window().getSize().height / 2);
            } catch (MalformedURLException e) {
                logger.log(Level.SEVERE, "Error initializing AndroidDriver", e);
                throw new RuntimeException("Failed to initialize AndroidDriver", e);  // Wrap it in RuntimeException
            }
        } else {
            logger.info("Reusing existing AndroidDriver session with session ID: " + driver.getSessionId());
        }
        return driver;
    }


    private static DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("appium:udid", Data.deviceId);
        desiredCapabilities.setCapability("appium:appActivity", Data.appActivity);
        desiredCapabilities.setCapability("appium:appPackage", Data.appPackage);
        desiredCapabilities.setCapability("appium:automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appium:noReset", true);
        desiredCapabilities.setCapability("appium:fullReset", false);
        desiredCapabilities.setCapability("appium:forceAppLaunch", true);
        desiredCapabilities.setCapability("appium:shouldTerminateApp", true);
        desiredCapabilities.setCapability("appium:newCommandTimeout", 3600);
//        desiredCapabilities.setCapability("appium:chromedriverExecutable", "/Users/phamanhduc/Documents/telegram-mobile-auto/src/main/resources/chromedriver-macos/chromedriver");
//        desiredCapabilities.setCapability("appium:chromedriver_autodownload", true);
        return desiredCapabilities;
    }

    public static void quitAndroidDriver() {
        if (driver != null) {
            logger.info("Previous session ID before quitting: " + driver.getSessionId());
            driver.quit();
            driver = null;
        }
    }

    public static WebElement waitUntilVisibleXpath(String xpath) {
        try {
            return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath(xpath)));
        } catch (TimeoutException e) {
            logger.log(Level.SEVERE, "Timeout: Element with XPath " + xpath + " was not visible in the given time.", e);
            throw e; // Optional: throw lại để xử lý tiếp ở các lớp khác
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "Element with XPath " + xpath + " not found.", e);
            throw e;
        }
    }

    public static List<WebElement> waitUntilAllVisibleXpath(String xpath) {
        try {
            return webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(AppiumBy.xpath(xpath)));
        } catch (TimeoutException e) {
            logger.log(Level.SEVERE, "Timeout: Elements with XPath " + xpath + " were not all visible in the given time.", e);
            throw e;
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "Elements with XPath " + xpath + " not found.", e);
            throw e;
        }
    }

    public static WebElement waitUntilVisibleId(String id) {
        try {
            return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId(id)));
        } catch (TimeoutException e) {
            logger.log(Level.SEVERE, "Timeout: Element with ID " + id + " was not visible in the given time.", e);
            throw e;
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "Element with ID " + id + " not found.", e);
            throw e;
        }
    }

    public static List<WebElement> waitUntilAllVisibleId(String id) {
        try {
            return webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(AppiumBy.accessibilityId(id)));
        } catch (TimeoutException e) {
            logger.log(Level.SEVERE, "Timeout: Elements with ID " + id + " were not all visible in the given time.", e);
            throw e;
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "Elements with ID " + id + " not found.", e);
            throw e;
        }
    }

    public static boolean isNoLongerVisibleXpath(String xpath) {
        try {
            return webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(AppiumBy.xpath(xpath)));
        } catch (TimeoutException e) {
            logger.log(Level.SEVERE, "Timeout: Elements with Xpath " + xpath + " were still visible in the given time.", e);
            throw e;
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "Xpath " + xpath + " not found.", e);
            throw e;
        }
    }

    public static boolean isElementXpathExist(String xpath) {
        try {
            WebElement element = waitUntilVisibleXpath(xpath);
            return element != null;
        } catch (Exception e) {
            return false;
        }
    }
}

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

public class AndroidDriverUtils {
    private static AndroidDriver driver;
    private static WebDriverWait webDriverWait;
    public static Map<String, Integer> middleScreenLocation;
    private static final Logger logger = Logger.getLogger(AndroidDriverUtils.class.getName());
    //Initialize logger here because AndroidDriverUtils will be initialized multiple times, and logger can log all games
    //If I only want to log the latest 6 games then initialize logger in getAndroidDriver()

    public static AndroidDriver getAndroidDriver() throws MalformedURLException {
        if(driver==null){
            System.out.println("Setting up driver for Telegram...");
            DesiredCapabilities desiredCapabilities = getDesiredCapabilities();

            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), desiredCapabilities);
            webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            middleScreenLocation = new HashMap<>();
            middleScreenLocation.put("x", driver.manage().window().getSize().width/2);
            middleScreenLocation.put("y", driver.manage().window().getSize().height/2);
            setupLogger();
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

    public static void quitAndroidDriver(){
        driver.quit();
        driver = null;
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
    public static boolean isElementXpathExist(String xpath){
        try {
            WebElement element = waitUntilVisibleXpath(xpath);
            return element != null;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static void setupLogger() {
        try {
            // Thiết lập ConsoleHandler để ghi log ra console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

            // Thiết lập FileHandler để ghi log vào file
            FileHandler fileHandler = new FileHandler("logs/logger.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);

            logger.setLevel(Level.ALL);  // Ghi tất cả các log
            logger.setUseParentHandlers(false);  // Tắt log mặc định để tránh in lặp lại

        } catch (IOException e) {
            System.err.println("Error setting up logger: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}

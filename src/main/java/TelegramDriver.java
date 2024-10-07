import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class TelegramDriver {
    private static AndroidDriver driver;
    private static WebDriverWait webDriverWait;
    private static Actions actions;

    //This method need to run first before any other methods
    public static AndroidDriver getAndroidDriver() throws MalformedURLException {
        if(driver==null){
            System.out.println("Setting up driver for MoonBix...");
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
//        desiredCapabilities.setCapability("appium:chromedriverExecutable", "/Users/phamanhduc/Documents/telegram-mobile-auto/src/main/resources/chromedriver-macos/chromedriver");
            desiredCapabilities.setCapability("appium:chromedriver_autodownload", true);
            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), desiredCapabilities);
            webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            actions = new Actions(driver);
        }
        return driver;
    }
    public static void quitAndroidDriver(){
        driver.quit();
    }

    public static WebElement waitUntilVisibleXpath(String xpath){
        return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath(xpath)));
    }
    public static WebElement waitUntilVisibleId(String xpath){
        return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId(xpath)));
    }

    public static void tap(WebElement element){
        actions.clickAndHold(element).pause(Duration.ofMillis(100)).release().perform();
        System.out.println("tap");
    }

    public static void tap(){
        actions.clickAndHold().pause(Duration.ofMillis(100)).release().perform();
    }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidDriverUtils {
    private static AndroidDriver driver;
    private static WebDriverWait webDriverWait;
    public static final Map<String, Integer> middleScreenLocation = new HashMap<>();;


    //This method need to run first before any other methods
    public static AndroidDriver getAndroidDriver() throws MalformedURLException {
        if(driver==null){
            System.out.println("Setting up driver for Telegram...");
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
//            desiredCapabilities.setCapability("appium:chromedriver_autodownload", true);

            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), desiredCapabilities);
            webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            middleScreenLocation.put("x", driver.manage().window().getSize().width/2);
            middleScreenLocation.put("y", driver.manage().window().getSize().height/2);
        }
        return driver;
    }
    public static void quitAndroidDriver(){
        driver.quit();
    }

    public static WebElement waitUntilVisibleXpath(String xpath){
        return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath(xpath)));
    }
    public static List<WebElement> waitUntilAllVisibleXpath(String xpath) {
        return webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(AppiumBy.xpath(xpath)));
    }
    public static WebElement waitUntilVisibleId(String Id){
        return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId(Id)));
    }
    public static List<WebElement> waitUntilAllVisibleId(String Id) {
        return webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(AppiumBy.accessibilityId(Id)));
    }
    public static boolean isElementXpathExist(String xpath){
        try {
            WebElement element = waitUntilVisibleXpath(xpath);
            return element != null; //trả về true nếu ko là null
        }
        catch (Exception e) {
            return false;
        }
    }

}

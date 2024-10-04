import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.Random;

public class MoonbixTest {
    AndroidDriver driver;
    WebDriverWait webDriverWait;
    Actions actions;
    int moonbixIndex = 1;
    String deviceId = "emulator-5554";
    String appActivity = "org.telegram.messenger.DefaultIcon";
    String appPackage = "org.telegram.messenger.web";
    String chatXpath = "//androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup["+ moonbixIndex +"]"; //need to change index
    String playButtonAccessibilityId = "Bot menu";
    String PlayGameButtonXpath = "//android.widget.TextView[@text='Play Game']";
    String PlayAgainButtonXpath = "//android.widget.Button[contains(@text,'Play Again')]";
    String backButtonXpath = "(//android.widget.Image)[1]";
    String shareWithFriendsXpath = "//android.widget.Button[@text='Share with Friends']";
    String availablePointsXpath = "//android.widget.TextView[@text='Available points']";


    @Test
    public void moonbixAuto() throws MalformedURLException, InterruptedException {
        System.out.println("Setting up driver for MoonBix...");
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("appium:udid", deviceId);
        desiredCapabilities.setCapability("appium:appActivity", appActivity);
        desiredCapabilities.setCapability("appium:appPackage", appPackage);
        desiredCapabilities.setCapability("appium:automationName", "UiAutomator2");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("fullReset", false);
        desiredCapabilities.setCapability("forceAppLaunch", true);
        desiredCapabilities.setCapability("shouldTerminateApp", true);
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), desiredCapabilities);
        actions = new Actions(driver);
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10L));
        System.out.println("Driver setup complete for MoonBix.");

        System.out.println("Starting test for MoonBix...");
        this.goToGame();
        int count = 0;
        Random rand = new Random();


        try {
            ApplicationState appState = this.driver.queryAppState(appPackage);
            if (appState != ApplicationState.RUNNING_IN_FOREGROUND) {
                ++count;
                System.out.println("MoonBix is not running, restarting...(" + count + ")");
                this.driver.activateApp(appPackage);
                this.goToGame();
            }

            goToGame();
            WebElement availPoints = waitUntilVisibleXpath(availablePointsXpath);
            WebElement playGameButton = waitUntilVisibleId(playButtonAccessibilityId);
            playGameButton.click();





        } catch (Exception var5) {
            System.out.println("Exception occurred: " + var5.getMessage());
            driver.quit();
            System.out.println("tool quits");
        }

    }

    public void tapAtCoordinates(int x, int y) {
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized.");
        } else {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = (new Sequence(finger, 1)).addAction(finger.createPointerMove(Duration.ofMillis(0L), PointerInput.Origin.viewport(), x, y)).addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg())).addAction(new Pause(finger, Duration.ofMillis(100L))).addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(tap));
        }
    }

    public void goToGame() throws InterruptedException {
        WebElement chat = waitUntilVisibleXpath(chatXpath);
        chat.click();
        WebElement playButton = waitUntilVisibleId(playButtonAccessibilityId);
        playButton.click();
        Thread.sleep(5000L);
    }

    public WebElement waitUntilVisibleXpath(String xpath){
        return this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath(xpath)));
    }
    public WebElement waitUntilVisibleId(String xpath){
        return this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId(xpath)));
    }
    public void tap(){
        actions.clickAndHold(element).pause(Duration.ofSeconds(2)).release().perform();
    }
}

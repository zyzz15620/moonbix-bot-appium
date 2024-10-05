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
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MoonbixTest {
    AndroidDriver driver;
    WebDriverWait webDriverWait;
    Actions actions;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    int tapCount = 0;
    int moonbixIndex = 1;
    Instant startTime;
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
    String available;
    String backToChatButtonXpath = "(//android.widget.ImageView[@content-desc='Go back'])[1]";
    //there are 2 buttons, one is back from chat, 2nd is back from the game

    String yourDailyRecordXpath = "//android.widget.TextView[@text='Your Daily Record']";
    String continueButtonXpath = "//android.widget.TextView[@text='Continue']";
    Map<String, Integer> middleScreenLocation = new HashMap<>();;


    @Test
    public void moonbixAuto() throws MalformedURLException, InterruptedException {
        System.out.println("Setting up driver for MoonBix...");
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("appium:udid", deviceId);
        desiredCapabilities.setCapability("appium:appActivity", appActivity);
        desiredCapabilities.setCapability("appium:appPackage", appPackage);
        desiredCapabilities.setCapability("appium:automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appium:noReset", true);
        desiredCapabilities.setCapability("appium:fullReset", false);
        desiredCapabilities.setCapability("appium:forceAppLaunch", true);
        desiredCapabilities.setCapability("appium:shouldTerminateApp", true);
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), desiredCapabilities);
        actions = new Actions(driver);
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        middleScreenLocation.put("x", driver.manage().window().getSize().width / 2);
        middleScreenLocation.put("y", driver.manage().window().getSize().height / 2);

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
            tap(playGameButton);





        } catch (Exception var5) {
            System.out.println("Exception occurred: " + var5.getMessage());
            driver.quit();
            System.out.println("tool quits");
        }

    }

//    public void tapAtCoordinates(int x, int y) {
//        if (driver == null) {
//            throw new IllegalStateException("Driver is not initialized.");
//        } else {
//            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
//            Sequence tap = (new Sequence(finger, 1))
//                    .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y))
//                    .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
//                    .addAction(new Pause(finger, Duration.ofMillis(100)))
//                    .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
//            driver.perform(Collections.singletonList(tap));
//        }
//    }
    public void tapAtCoordinates(int x, int y) {
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized.");
        } else {
            Actions actions = new Actions(driver);
            actions.moveByOffset(x, y)
                    .clickAndHold()
                    .pause(Duration.ofMillis(100))
                    .release()
                    .perform();
        }
    }

    public void goToGame() throws InterruptedException {
        waitUntilVisibleXpath(chatXpath).click();
        waitUntilVisibleId(playButtonAccessibilityId).click();
    }

    public WebElement waitUntilVisibleXpath(String xpath){
        return this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath(xpath)));
    }
    public WebElement waitUntilVisibleId(String xpath){
        return this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId(xpath)));
    }
    public void tap(WebElement element){
        actions.clickAndHold(element).pause(Duration.ofMillis(100)).release().perform();
    }
    public void tap(){
        actions.clickAndHold().pause(Duration.ofMillis(100)).release().perform();
    }
    public void playGame(Integer x, Integer y) throws InterruptedException {
        actions.moveByOffset(x, y);
        //wait until to tap
        Runnable tapTask = new Runnable() {
            @Override
            public void run() {
                if (tapCount >= 25 || Duration.between(startTime, Instant.now()).getSeconds() >= 50) {
                    System.out.println("Stop in:" + tapCount + " taps or 50s");
                    scheduler.shutdown();
                    return;
                }
                tap();
                tapCount++;
                scheduler.schedule(this, 1900, TimeUnit.MILLISECONDS);
            }
        };
        scheduler.schedule(tapTask, 0, TimeUnit.MILLISECONDS);
    }
    public void playAllGames(Integer x, Integer y) throws InterruptedException {
        WebElement inspectingElement = waitUntilVisibleXpath("(//android.widget.Button)[]"); //what index?
        if(Objects.equals(inspectingElement.getAttribute("text"), "Share with Friends")){
            //exit game
        } else if (inspectingElement.getAttribute("text").contains("Play Again")){
            tap(inspectingElement);
            playGame(x, y);
        }
    }
    public boolean remainingGameAttempts(){
        return true;
    }
    public void byPassYourDailyRecordScreen(){

    }
}

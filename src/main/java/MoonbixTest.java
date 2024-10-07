import io.appium.java_client.AppiumBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoonbixTest {
    AndroidDriver driver;
    WebDriverWait webDriverWait;
    Actions actions;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = Logger.getLogger(MoonbixTest.class.getName());

    int tapCount = 0;
    Instant startTime;
    Map<String, Integer> middleScreenLocation = new HashMap<>();;


    public static void main(String[] args) throws InterruptedException, MalformedURLException {
        MoonbixTest test = new MoonbixTest();
        test.moonbixAuto();  // Gọi phương thức để chạy tự động hóa
    }

    public void moonbixAuto() throws InterruptedException, MalformedURLException {
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
        actions = new Actions(driver);
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        middleScreenLocation.put("x", driver.manage().window().getSize().width / 2);
        middleScreenLocation.put("y", driver.manage().window().getSize().height / 2);
        System.out.println("Driver setup complete for MoonBix.");

        this.goToGame();
        waitUntilVisibleXpath(Data.PlayGameButtonXpath).click();
        System.out.println("check1");

        Runnable playAllGamesTask = new Runnable() {
            @Override
            public void run() {
                try {
                    playAllGames(); // Gọi phương thức playAllGames()
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Exception in playAllGamesTask", e);
                }
            }
        };
        scheduler.scheduleAtFixedRate(playAllGamesTask, 0, 1, TimeUnit.HOURS);
        System.out.println("check2");
    }


    public void tapAtCoordinates(int x, int y) {
        try {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

            Sequence tapSequence = new Sequence(finger, 1);
            tapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));  // Di chuyển đến tọa độ x, y
            tapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));  // Nhấn xuống
            tapSequence.addAction(new Pause(finger, Duration.ofMillis(100)));  // Dừng lại trong 100ms
            tapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));  // Nhả ra
            driver.perform(Collections.singletonList(tapSequence));

        } catch (Exception e) {
            System.out.println("Error while tapping at coordinates: " + e.getMessage());
        }
    }


    public void goToGame() throws InterruptedException {
        waitUntilVisibleXpath(Data.chatXpath).click();
        waitUntilVisibleId(Data.startGameButtonId).click();
    }

    public WebElement waitUntilVisibleXpath(String xpath){
        return this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath(xpath)));
    }
    public WebElement waitUntilVisibleId(String xpath){
        return this.webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId(xpath)));
    }
    public void tap(WebElement element){
        actions.clickAndHold(element).pause(Duration.ofMillis(100)).release().perform();
        System.out.println("tap");
    }
    public void tap(){
        actions.clickAndHold().pause(Duration.ofMillis(100)).release().perform();
    }
    private void playGame() throws InterruptedException {
        tapCount = 0;
        int x = middleScreenLocation.get("x");
        int y = middleScreenLocation.get("y");
        ScheduledExecutorService schedulerTap = Executors.newScheduledThreadPool(1);
        System.out.println("Starting game with coordinates: " + x + ", " + y);

        Runnable tapTask = new Runnable() {
            @Override
            public void run() {
                try {
                    if (tapCount >= 25) {
                        System.out.println("Finished after " + tapCount + " taps.");
                        schedulerTap.shutdown();
                        return;
                    }
                    tapAtCoordinates(x, y);
                    System.out.println("tapAtCoordinates: " + x + ", " + y);
                    tapCount++;
                    System.out.println("Tap #" + tapCount);
                } catch (Exception e) {
                    System.out.println("Error occurred during tapping: " + e.getMessage());
                    schedulerTap.shutdown(); // Shutdown nếu có lỗi xảy ra
                }
            }
        };

        // Thay đổi cách lên lịch với scheduleAtFixedRate
        schedulerTap.scheduleAtFixedRate(tapTask, 0, 1900, TimeUnit.MILLISECONDS);

        try {
            // Chờ cho đến khi scheduler hoàn thành hoặc hết thời gian chờ
            boolean terminated = schedulerTap.awaitTermination(2, TimeUnit.MINUTES);
            if (!terminated) {
                System.out.println("The scheduled taps did not finish in the expected time.");
                schedulerTap.shutdownNow();  // Tắt ngay nếu hết thời gian chờ
            }
        } catch (InterruptedException e) {
            System.out.println("Error occurred during termination waiting: " + e.getMessage());
            schedulerTap.shutdownNow();  // Đảm bảo scheduler được tắt nếu gặp lỗi
            Thread.currentThread().interrupt();  // Khôi phục trạng thái gián đoạn
        }
    }



    public void playAllGames() throws InterruptedException {
        for (int i = 0; i < 6; i++) {
            playGame();

            boolean buttonClicked = false;
            int retryCount = 0;
            int maxRetries = 5;
            long retryDelay = 2000; // Thời gian chờ 2 giây giữa mỗi lần kiểm tra

            while (!buttonClicked && retryCount < maxRetries) {
                try {
                    // Kiểm tra nút "Play Again"
                    if (waitUntilVisibleXpath(Data.playAgainXpath).isDisplayed()) {
                        System.out.println("play again button displayed");
                        waitUntilVisibleXpath(Data.playAgainXpath).click();
                        buttonClicked = true;
                    }
                    // Kiểm tra nút "Continue"
                    else if (waitUntilVisibleXpath("(//android.widget.Button)[2]").isDisplayed()) {
                        System.out.println("continue to home button displayed");
                        waitUntilVisibleXpath("(//android.widget.Button)[2]").click();
                        buttonClicked = true;
                        break;
                    }
                    else {
                        System.out.println("no button displayed");
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Retry #" + retryCount + " - Button not yet visible.");
                }

                retryCount++;
                Thread.sleep(retryDelay);
            }

            if (!buttonClicked) {
                System.out.println("No button was clicked after retrying " + maxRetries + " times.");
            }
        }
    }


    public boolean remainingGameAttempts(){
        return true;
    }
    public void byPassYourDailyRecordScreen(){
        if(waitUntilVisibleXpath(Data.yourDailyRecordXpath).isDisplayed()){
            tap(waitUntilVisibleXpath(Data.continueButtonXpath));
        }
    }
}

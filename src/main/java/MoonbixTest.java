import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoonbixTest {
    AndroidDriver driver;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = Logger.getLogger(MoonbixTest.class.getName());

    int tapCount = 0;
    Map<String, Integer> middleScreenLocation = new HashMap<>();;

    public static void main(String[] args) throws InterruptedException, MalformedURLException {
        MoonbixTest test = new MoonbixTest();
        test.moonbixAuto();
    }

    public void moonbixAuto() throws InterruptedException, MalformedURLException {
        driver = TelegramDriver.getAndroidDriver();
        System.out.println("Driver setup complete for MoonBix.");

        this.goToGame();
        TelegramDriver.waitUntilVisibleXpath(Data.PlayGameButtonXpath).click();
        System.out.println("check1");

        Runnable playAllGamesTask = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Running playAllGamesTask");
                    playAllGames();
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
            tapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
            tapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tapSequence.addAction(new Pause(finger, Duration.ofMillis(100)));
            tapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(tapSequence));

        } catch (Exception e) {
            System.out.println("Error while tapping at coordinates: " + e.getMessage());
        }
    }


    public void goToGame() throws InterruptedException {
        TelegramDriver.waitUntilVisibleXpath(Data.chatXpath).click();
        TelegramDriver.waitUntilVisibleId(Data.startGameButtonId).click();
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
                    if (TelegramDriver.waitUntilVisibleXpath(Data.playAgainXpath).isDisplayed()) {
                        System.out.println("play again button displayed");
                        TelegramDriver.waitUntilVisibleXpath(Data.playAgainXpath).click();
                        buttonClicked = true;
                    }
                    // Kiểm tra nút "Continue"
                    else if (TelegramDriver.waitUntilVisibleXpath("(//android.widget.Button)[2]").isDisplayed()) {
                        System.out.println("continue to home button displayed");
                        TelegramDriver.waitUntilVisibleXpath("(//android.widget.Button)[2]").click();
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
        if(TelegramDriver.waitUntilVisibleXpath(Data.yourDailyRecordXpath).isDisplayed()){
            TelegramDriver.tap(TelegramDriver.waitUntilVisibleXpath(Data.continueButtonXpath));
        }
    }
}

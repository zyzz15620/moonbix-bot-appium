import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoonbixTest {
    AndroidDriver driver;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = Logger.getLogger(MoonbixTest.class.getName());
    private final static int delayTap = 2000;
    private static int x, y;

    int tapCount = 0;

    public static void main(String[] args) throws InterruptedException, MalformedURLException {
        MoonbixTest test = new MoonbixTest();
        test.moonbixAuto();
    }

    public void moonbixAuto() throws InterruptedException, MalformedURLException {
        driver = AndroidDriverUtils.getAndroidDriver();
        x = AndroidDriverUtils.middleScreenLocation.get("x");
        y = AndroidDriverUtils.middleScreenLocation.get("y");
        System.out.println("Driver setup complete for MoonBix.");


        Runnable playAllGamesTask = new Runnable() {
            @Override
            public void run() {
                try {
                    goToGame();
                    byPassYourDailyRecordScreen();
                    checkLeaderBoardWidget();
                    checkFriendsWidget();
                    checkSurpriseWidget();
                    checkTasksWidget();

                    clickHomeWidget();
                    AndroidDriverUtils.waitUntilVisibleXpath(Data.PlayGameButtonXpath).click();

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



    public void goToGame() throws InterruptedException {
        AndroidDriverUtils.waitUntilVisibleXpath(Data.chatXpath).click();
        AndroidDriverUtils.waitUntilVisibleId(Data.startGameButtonId).click();
    }


    private void playGame() throws InterruptedException {
        tapCount = 0;

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
                    ActionsUtils.tapAtCoordinates(x, y);
                    System.out.println("tapAtCoordinates: " + x + ", " + y);
                    tapCount++;
                    System.out.println("Tap #" + tapCount);
                } catch (Exception e) {
                    System.out.println("Error occurred during tapping: " + e.getMessage());
                    e.printStackTrace();
                    schedulerTap.shutdown(); // Shutdown nếu có lỗi xảy ra
                }
            }
        };
        schedulerTap.scheduleAtFixedRate(tapTask, 0, delayTap-100 , TimeUnit.MILLISECONDS);

        try {
            // Chờ cho đến khi scheduler hoàn thành hoặc hết thời gian chờ
            boolean terminated = schedulerTap.awaitTermination(2, TimeUnit.MINUTES);
            if (!terminated) {
                System.out.println("The scheduled taps did not finish in the expected time.");
                schedulerTap.shutdownNow();  // Tắt ngay nếu hết thời gian chờ
            }
        } catch (InterruptedException e) {
            System.out.println("Error occurred during termination waiting: " + e.getMessage());
            e.printStackTrace();
            schedulerTap.shutdownNow();  // Đảm bảo scheduler được tắt nếu gặp lỗi
            Thread.currentThread().interrupt();  // Khôi phục trạng thái gián đoạn
        }
    }

    public void playAllGames() throws InterruptedException {
        for (int i = 0; i < 6; i++) {
            System.out.println("Starting game #" + (i + 1));
            playGame();
            System.out.println("Game #" + (i + 1) + " finished.");

            boolean buttonClicked = false;
            int retryCount = 0;
            int maxRetries = 5;
            long retryDelay = 2000; // Thời gian chờ 2 giây giữa mỗi lần kiểm tra

            while (!buttonClicked && retryCount < maxRetries) {
                try {
                    if (AndroidDriverUtils.isElementXpathExist(Data.playAgainXpath)) {
                        System.out.println("Play Again button is visible after game #" + (i + 1) + ", clicking...");
                        AndroidDriverUtils.waitUntilVisibleXpath(Data.playAgainXpath).click();
                        buttonClicked = true;
                        break;
                    }
                    // Kiểm tra nút "Continue"
                    else if (AndroidDriverUtils.isElementXpathExist("(//android.widget.Button)[2]")) {
                        AndroidDriverUtils.waitUntilVisibleXpath("(//android.widget.Button)[2]").click();
                        AndroidDriverUtils.waitUntilVisibleXpath(Data.goBackXpath);
                        System.out.println("Continue button is visible after game #" + (i + 1) + ", clicking Continue button and back to home");
                        return;
                    } else {
                        System.out.println("No relevant button (Play Again or Continue) found after game #" + (i + 1));
                        break;  // Không có nút nào hiện ra, thoát vòng lặp while
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("Retry #" + retryCount + " for game #" + (i + 1) + ": Element not found.");
                } catch (TimeoutException e) {
                    System.out.println("Retry #" + retryCount + " for game #" + (i + 1) + ": Timeout while waiting for element.");
                } catch (Exception e) {
                    System.out.println("Unexpected error during retry #" + retryCount + " for game #" + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
                retryCount++;
                Thread.sleep(retryDelay);
            }
            if (!buttonClicked) {
                System.out.println("No button was clicked after retrying " + maxRetries + " times for game #" + (i + 1) + ".");
            }
        }
    }





    public boolean remainingGameAttempts(){
        return true;
    }
    public void clickHomeWidget(){
        AndroidDriverUtils.waitUntilVisibleXpath(Data.gameWidgetXpath).click();

    }
    public void byPassYourDailyRecordScreen(){
        if(AndroidDriverUtils.isElementXpathExist(Data.yourDailyRecordXpath)){
            AndroidDriverUtils.waitUntilVisibleXpath(Data.continueButtonXpath).click();
        }
        System.out.println("passed Your Daily Record screen");
    }
    public void checkLeaderBoardWidget(){
        AndroidDriverUtils.waitUntilVisibleXpath(Data.leaderboardWidgetXpath).click();
        ActionsUtils.swipe(x, y+200, x, y-200, Duration.ofSeconds(1) );
        ActionsUtils.swipe(x, y+200, x, y-200, Duration.ofSeconds(1) );
        ActionsUtils.swipe(x, y-200, x, y+200, Duration.ofSeconds(1) );
        ActionsUtils.swipe(x, y-200, x, y+200, Duration.ofSeconds(1) );
    }
    public void checkTasksWidget() throws InterruptedException {
        AndroidDriverUtils.waitUntilVisibleXpath(Data.taskWidgetXpath).click();

        if(AndroidDriverUtils.isElementXpathExist(Data.unfinishedTasksListXpath)) {
            List<WebElement> tasks = AndroidDriverUtils.waitUntilAllVisibleXpath(Data.unfinishedTasksListXpath);
            for (WebElement task : tasks) {
                task.click();
                Thread.sleep(2000);
                ActionsUtils.swipe(1, y, 350, y, Duration.ofSeconds(1));
            }
        }
        System.out.println("tasks done");
    }
    public void checkFriendsWidget(){
        AndroidDriverUtils.waitUntilVisibleXpath(Data.friendsWidgetXpath).click();
    }
    public void checkSurpriseWidget(){
        AndroidDriverUtils.waitUntilVisibleXpath(Data.surpriseWidgetXpath).click();

    }
}

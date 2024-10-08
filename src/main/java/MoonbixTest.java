import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.net.MalformedURLException;
import java.time.Duration;
import java.time.LocalDateTime;
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
                    System.out.println("Starting playAllGamesTask at: " + LocalDateTime.now());
                    logger.info("Starting playAllGamesTask at: " + LocalDateTime.now());

                    goToGame();
                    byPassYourDailyRecordScreen();
                    checkLeaderBoardWidget();
                    checkFriendsWidget();
                    checkSurpriseWidget();
                    checkTasksWidget();

                    clickHomeWidget();
                    ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.PlayGameButtonXpath));

                    System.out.println("Running playAllGamesTask");
                    playAllGames();
                    System.out.println("Completed playAllGamesTask at: " + LocalDateTime.now());
                    logger.info("Completed playAllGamesTask at: " + LocalDateTime.now());
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Exception in playAllGamesTask", e);
                }
            }
        };
        scheduler.scheduleAtFixedRate(playAllGamesTask, 0, 56, TimeUnit.MINUTES);
        System.out.println("MoonBix automation task scheduled to run every hour.");
    }



    public void goToGame() throws InterruptedException {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.chatXpath));
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleId(Data.startGameButtonId));
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
                    schedulerTap.shutdown();
                }
            }
        };
        schedulerTap.scheduleAtFixedRate(tapTask, 0, delayTap-150 , TimeUnit.MILLISECONDS);

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
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.playAgainXpath));
                        buttonClicked = true;
                        break;
                    }
                    // Kiểm tra nút "Continue"
                    else if (AndroidDriverUtils.isElementXpathExist("(//android.widget.Button)[2]")) {
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath("(//android.widget.Button)[2]"));
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.goBackXpath));
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
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.gameWidgetXpath));

    }
    public void byPassYourDailyRecordScreen(){
        if(AndroidDriverUtils.isElementXpathExist(Data.yourDailyRecordXpath)){
            ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.continueButtonXpath));
        }
        System.out.println("passed Your Daily Record screen");
    }
    public void checkLeaderBoardWidget() throws InterruptedException {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.leaderboardWidgetXpath));
        Thread.sleep(1);
        ActionsUtils.swipe(x, y+200, x, y-200, Duration.ofMillis(700) );
        ActionsUtils.swipe(x, y+200, x, y-200, Duration.ofMillis(700) );
        ActionsUtils.swipe(x, y-200, x, y+200, Duration.ofMillis(700) );
        ActionsUtils.swipe(x, y-200, x, y+200, Duration.ofMillis(700) );
    }
    public void checkTasksWidget() throws InterruptedException {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.taskWidgetXpath));

        if(AndroidDriverUtils.isElementXpathExist(Data.unfinishedTasksListXpath)) {
            List<WebElement> tasks = AndroidDriverUtils.waitUntilAllVisibleXpath(Data.unfinishedTasksListXpath);
            for (WebElement task : tasks) {
                ActionsUtils.tapElement(task);
                Thread.sleep(2000);
                ActionsUtils.swipe(1, y, 350, y, Duration.ofMillis(700));
            }
        }
        System.out.println("tasks done");
    }
    public void checkFriendsWidget(){
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.friendsWidgetXpath));
    }
    public void checkSurpriseWidget(){
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.surpriseWidgetXpath));

    }
}

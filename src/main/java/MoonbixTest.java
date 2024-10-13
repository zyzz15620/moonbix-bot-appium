import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class MoonbixTest {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = Logger.getLogger(MoonbixTest.class.getName());
    private final static int delayTap = 2000;
    private static int x, y;
    private static final int maximumTimeAGame = 2;

    int tapCount = 0;

    public static void main(String[] args){
        MoonbixTest test = new MoonbixTest();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                test.moonbixAuto();
            } catch (InterruptedException | MalformedURLException e) {
                logger.log(Level.SEVERE, "Exception occurred while running moonbixAuto", e);
            }
        }, 0, 60, TimeUnit.MINUTES);
    }

    public void moonbixAuto() throws InterruptedException, MalformedURLException {
        AndroidDriverUtils.getAndroidDriver();
        x = AndroidDriverUtils.middleScreenLocation.get("x");
        y = AndroidDriverUtils.middleScreenLocation.get("y");
        logger.info("Driver set-up complete.");

        try {
            logger.info("Running goToGame()... ");

            goToGame();
            byPassYourDailyRecordScreen();
            checkLeaderBoardWidget();
            checkFriendsWidget();
            checkSurpriseWidget();
            checkTasksWidget();

            clickHomeWidget();
            ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.PlayGameButtonXpath));

            logger.info("Running playAllGames()...");
            playAllGames();
            logger.info("Completed playAllGamesTask.");
            AndroidDriverUtils.quitAndroidDriver();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Exception in playAllGamesTask.", e);
        }
    }

    private static String getTimeNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-M-yyyy HH:mm:ss"));
    }


    public void goToGame() throws InterruptedException {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.chatXpath));
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleId(Data.startGameButtonId));
    }


    private void playGame() {
        tapCount = 0;
        ScheduledExecutorService schedulerTap = Executors.newScheduledThreadPool(1);
        Runnable tapTask = getRunnable(schedulerTap);
        schedulerTap.scheduleAtFixedRate(tapTask, 0, delayTap - 150, TimeUnit.MILLISECONDS);
        checkIfAGameTookToLong(schedulerTap);
    }

    private static void checkIfAGameTookToLong(ScheduledExecutorService schedulerTap) {
        try {
            boolean terminated = schedulerTap.awaitTermination(maximumTimeAGame, TimeUnit.MINUTES);
            if (!terminated) {
                logger.log(Level.WARNING, "The game did not finish in " + maximumTimeAGame + " minutes" );
                schedulerTap.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Error occurred during termination waiting: " + e.getMessage());
            e.printStackTrace();
            schedulerTap.shutdownNow();
            Thread.currentThread().interrupt();  // Khôi phục trạng thái gián đoạn
        }
    }

    private Runnable getRunnable(ScheduledExecutorService schedulerTap) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    if (tapCount >= 50/((delayTap)/1000)) { //each game has 50s to play so number of taps is around 50/delayTap
                        logger.info("Finished a game after " + tapCount + " taps.");
                        schedulerTap.shutdown();
                        return;
                    }
                    ActionsUtils.tapAtCoordinates(x, y);
                    tapCount++;
                    logger.info("Tap #" + tapCount + " performed");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error occurred during tapping: " + e.getMessage() );
                    e.printStackTrace();
                    schedulerTap.shutdown();
                }
            }
        };
    }

    public void playAllGames() throws InterruptedException {
        for (int i = 0; i < 6; i++) {
            logger.info("Starting game #" + (i + 1));
            playGame();
            logger.info("Game #" + (i + 1) + " finished.");

            boolean buttonClicked = false;
            int retryCount = 0;
            int maxRetries = 5;
            long retryDelay = 2000;

            while (!buttonClicked && retryCount < maxRetries) {
                try {
                    if (AndroidDriverUtils.isElementXpathExist(Data.playAgainXpath)) {
                        logger.info("Play Again button still visible after game #" + (i + 1) + ", clicking...");
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.playAgainXpath));
                        buttonClicked = true;
                        break;
                    }
                    // Kiểm tra nút "Continue"
                    else if (AndroidDriverUtils.isElementXpathExist("(//android.widget.Button)[2]")) {
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath("(//android.widget.Button)[2]"));
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.goBackXpath));
                        logger.info("Continue button is visible after game #" + (i + 1) + ", clicking Continue button and back to home");
                        return;
                    } else {
                        logger.log(Level.WARNING ,"No relevant button (Play Again or Continue) found after game #" + (i + 1));
                        break;  // Không có nút nào hiện ra, thoát vòng lặp while
                    }
                } catch (NoSuchElementException e) {
                    logger.log(Level.WARNING, "Retry #" + retryCount + " for game #" + (i + 1) + ": Element not found.");
                } catch (TimeoutException e) {
                    logger.log(Level.WARNING, "Retry #" + retryCount + " for game #" + (i + 1) + ": Timeout while waiting for element.");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Unexpected error during retry #" + retryCount + " for game #" + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
                retryCount++;
                Thread.sleep(retryDelay);
            }
            if (!buttonClicked) {
                logger.log(Level.WARNING, "No button was clicked after retrying " + maxRetries + " times for game #" + (i + 1) + ".");
            }
        }
    }

    public void clickHomeWidget() {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.gameWidgetXpath));
        logger.info("Go to Home screen");
    }

    public void byPassYourDailyRecordScreen() {
        if (AndroidDriverUtils.isElementXpathExist(Data.yourDailyRecordXpath)) {
            ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.continueButtonXpath));
        }
        logger.info("Daily Record screen checked");
    }

    public void checkLeaderBoardWidget() throws InterruptedException {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.leaderboardWidgetXpath));
        Thread.sleep(1);
        ActionsUtils.swipe(x, y + 200, x, y - 200, Duration.ofMillis(700));
        ActionsUtils.swipe(x, y + 200, x, y - 200, Duration.ofMillis(700));
        ActionsUtils.swipe(x, y - 200, x, y + 200, Duration.ofMillis(700));
        ActionsUtils.swipe(x, y - 200, x, y + 200, Duration.ofMillis(700));
        logger.info("Leaderboard checked");
    }

    public void checkTasksWidget() throws InterruptedException {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.taskWidgetXpath));

        if (AndroidDriverUtils.isElementXpathExist(Data.unfinishedTasksListXpath)) {
            List<WebElement> tasks = AndroidDriverUtils.waitUntilAllVisibleXpath(Data.unfinishedTasksListXpath);
            for (WebElement task : tasks) {
                ActionsUtils.tapElement(task);
                Thread.sleep(2000);
                ActionsUtils.swipe(1, y, 350, y, Duration.ofMillis(700));
            }
        }
        logger.info("Tasks checked");

    }

    public void checkFriendsWidget() {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.friendsWidgetXpath));
        logger.info("Friends checked");
    }

    public void checkSurpriseWidget() {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.surpriseWidgetXpath));
        logger.info("Surprise checked");
    }


}

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class MoonbixTest {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final static int delayTap = 2000;
    private static int x, y;
    private static final int maximumTimeAGame = 2;

    int tapCount = 0;

    public static void main(String[] args) {
        MoonbixTest test = new MoonbixTest();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                test.moonbixAuto();
            } catch (InterruptedException e) {
                DriverLogger.getLogger().log(Level.SEVERE, "Exception occurred while running moonbixAuto", e);
            }
        }, 0, 60, TimeUnit.MINUTES);
    }

    public void moonbixAuto() throws InterruptedException {
        AndroidDriverUtils.getAndroidDriver();
        x = AndroidDriverUtils.middleScreenLocation.get("x");
        y = AndroidDriverUtils.middleScreenLocation.get("y");
        DriverLogger.getLogger().info("Driver set-up complete.");

        try {
            DriverLogger.getLogger().info("Running goToGame()... ");

            goToGame();
            byPassYourDailyRecordScreen();
            checkLeaderBoardWidget();
            checkFriendsWidget();
            checkSurpriseWidget();
            checkTasksWidget();
            clickHomeWidget();
            ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.PlayGameButtonXpath));

            DriverLogger.getLogger().info("Running playAllGames()...");
            playAllGames();
            DriverLogger.getLogger().info("Completed playAllGamesTask.");
            AndroidDriverUtils.quitAndroidDriver();
        } catch (InterruptedException e) {
            DriverLogger.getLogger().log(Level.SEVERE, "Exception in playAllGamesTask.", e);
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
                DriverLogger.getLogger().log(Level.WARNING, "The game did not finish in " + maximumTimeAGame + " minutes");
                schedulerTap.shutdownNow();
            }
        } catch (InterruptedException e) {
            DriverLogger.getLogger().log(Level.WARNING, "Error occurred during termination waiting: " + e.getMessage());
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
                    if (tapCount >= 50 / ((delayTap) / 1000)) { //each game has 50s to play so number of taps is around 50/delayTap
                        DriverLogger.getLogger().info("Finished a game after " + tapCount + " taps.");
                        schedulerTap.shutdown();
                        return;
                    }
                    ActionsUtils.tapAtCoordinates(x, y);
                    tapCount++;
                    DriverLogger.getLogger().info("Tap #" + tapCount + " performed");
                } catch (Exception e) {
                    DriverLogger.getLogger().log(Level.SEVERE, "Error occurred during tapping: " + e.getMessage());
                    e.printStackTrace();
                    schedulerTap.shutdown();
                }
            }
        };
    }

    public void playAllGames() throws InterruptedException {
        for (int i = 0; i < 6; i++) {
            DriverLogger.getLogger().info("Starting game #" + (i + 1));
            playGame();
            DriverLogger.getLogger().info("Game #" + (i + 1) + " finished.");

            boolean buttonClicked = false;
            int retryCount = 0;
            int maxRetries = 5;
            long retryDelay = 2000;

            while (!buttonClicked && retryCount < maxRetries) {
                try {
                    if (AndroidDriverUtils.isElementXpathExist(Data.playAgainXpath)) {
                        DriverLogger.getLogger().info("Play Again button still visible after game #" + (i + 1) + ", clicking...");
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.playAgainXpath));
                        buttonClicked = true;
                        break;
                    }
                    // Kiểm tra nút "Continue"
                    else if (AndroidDriverUtils.isElementXpathExist("(//android.widget.Button)[2]")) {
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath("(//android.widget.Button)[2]"));
                        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.goBackXpath));
                        DriverLogger.getLogger().info("Continue button is visible after game #" + (i + 1) + ", clicking Continue button and back to home");
                        return;
                    } else {
                        DriverLogger.getLogger().log(Level.WARNING, "No relevant button (Play Again or Continue) found after game #" + (i + 1));
                        break;  // Không có nút nào hiện ra, thoát vòng lặp while
                    }
                } catch (NoSuchElementException e) {
                    DriverLogger.getLogger().log(Level.WARNING, "Retry #" + retryCount + " for game #" + (i + 1) + ": Element not found.");
                } catch (TimeoutException e) {
                    DriverLogger.getLogger().log(Level.WARNING, "Retry #" + retryCount + " for game #" + (i + 1) + ": Timeout while waiting for element.");
                } catch (Exception e) {
                    DriverLogger.getLogger().log(Level.SEVERE, "Unexpected error during retry #" + retryCount + " for game #" + (i + 1) + ": " + e.getMessage());
                    e.printStackTrace();
                }
                retryCount++;
                Thread.sleep(retryDelay);
            }
            if (!buttonClicked) {
                DriverLogger.getLogger().log(Level.WARNING, "No button was clicked after retrying " + maxRetries + " times for game #" + (i + 1) + ".");
            }
        }
    }

    public void clickHomeWidget() {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.gameWidgetXpath));
        DriverLogger.getLogger().info("Go to Home screen");
    }

    public void byPassYourDailyRecordScreen() {
        if (AndroidDriverUtils.isElementXpathExist(Data.yourDailyRecordXpath)) {
            ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.continueButtonXpath));
        }
        DriverLogger.getLogger().info("Daily Record screen checked");
    }

    public void checkLeaderBoardWidget() throws InterruptedException {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.leaderboardWidgetXpath));
        Thread.sleep(1);
        ActionsUtils.swipe(x, y + 200, x, y - 200, Duration.ofMillis(700));
        ActionsUtils.swipe(x, y + 200, x, y - 200, Duration.ofMillis(700));
        ActionsUtils.swipe(x, y - 200, x, y + 200, Duration.ofMillis(700));
        ActionsUtils.swipe(x, y - 200, x, y + 200, Duration.ofMillis(700));
        DriverLogger.getLogger().info("Leaderboard checked");
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
        DriverLogger.getLogger().info("Tasks checked");

    }

    public void checkFriendsWidget() {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.friendsWidgetXpath));
        DriverLogger.getLogger().info("Friends checked");
    }

    public void checkSurpriseWidget() {
        ActionsUtils.tapElement(AndroidDriverUtils.waitUntilVisibleXpath(Data.surpriseWidgetXpath));
        DriverLogger.getLogger().info("Surprise checked");
    }
}

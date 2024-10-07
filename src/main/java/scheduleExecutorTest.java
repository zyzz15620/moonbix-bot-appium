import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class scheduleExecutorTest {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable taskWrapper = new Runnable() {
            private int tabCounter = 0;  // Đếm số lần in ra 25 lần
            private int gameCounter = 0;  // Đếm số chu kỳ lớn (6 lần in 25 lần)

            @Override
            public void run() {
                if (tabCounter < 25) {
                    System.out.println("In ra sau mỗi 2 giây: Lần " + (tabCounter + 1));
                    tabCounter++;
                    scheduler.schedule(this, 2, TimeUnit.SECONDS);
                } else if (gameCounter < 6) {
                    // Sau khi in ra đủ 25 lần, tăng chu kỳ và bắt đầu lại
                    System.out.println("Hoàn thành 25 lần, bắt đầu chu kỳ tiếp theo.");
                    tabCounter = 0; // Đặt lại bộ đếm in ra
                    gameCounter++;    // Tăng số lần chu kỳ lớn
                    scheduler.schedule(this, 2, TimeUnit.SECONDS); // Bắt đầu lại sau 2 giây
                } else {
                    // Sau 6 chu kỳ, nghỉ 1 tiếng
                    System.out.println("Hoàn thành 6 chu kỳ lớn, nghỉ 1 tiếng.");
                    tabCounter = 0; // Đặt lại bộ đếm in ra
                    gameCounter = 0; // Đặt lại bộ đếm chu kỳ lớn
                    scheduler.schedule(this, 1, TimeUnit.HOURS); // Nghỉ 1 tiếng
                }
            }
        };

        // Bắt đầu lần đầu tiên sau 2 giây
        scheduler.schedule(taskWrapper, 2, TimeUnit.SECONDS);
    }
}

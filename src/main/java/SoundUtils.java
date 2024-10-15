import java.awt.*;

public class SoundUtils {
    public static void alert() {
        int repeatCount = 5;
        int delay = 1000;
        for (int i = 0; i < repeatCount; i++) {
            Toolkit.getDefaultToolkit().beep();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

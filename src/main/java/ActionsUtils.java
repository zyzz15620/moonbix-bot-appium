import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

public class ActionsUtils {
    private static final AndroidDriver driver;

    static {
        try {
            driver = AndroidDriverUtils.getAndroidDriver();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Point getElementCenter(WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }

        int centerX = element.getLocation().getX() + (element.getSize().getWidth() / 2);
        int centerY = element.getLocation().getY() + (element.getSize().getHeight() / 2);

        return new Point(centerX, centerY);
    }

    private static void validateCoordinates(int x, int y) {
        if (x == 0 && y == 0) {
            throw new IllegalArgumentException("Invalid coordinates: both x and y are zero");
        }
    }

    public static void tapElement(WebElement element, Duration duration) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        Point center = getElementCenter(element);
        int x = center.getX();
        int y = center.getY();
        validateCoordinates(x, y);
        executeTap(x, y, duration);
    }

    public static void tapElement(WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        Point center = getElementCenter(element);
        int x = center.getX();
        int y = center.getY();
        validateCoordinates(x, y);
        executeTap(x, y, Duration.ofMillis(150));
    }

    public static void tapAtCoordinates(int x, int y) {
        try {
            validateCoordinates(x, y);
            executeTap(x, y, Duration.ofMillis(150));
        } catch (Exception e) {
            handleException(e, "tapping at coordinates");
        }
    }

    public static void longPressAtCoordinates(int x, int y, Duration duration) {
        try {
            validateCoordinates(x, y);
            executeTap(x, y, duration);
        } catch (Exception e) {
            handleException(e, "long pressing at coordinates");
        }
    }

    public static void swipe(int startX, int startY, int endX, int endY, Duration duration) {
        try {
            validateCoordinates(startX, startY);
            validateCoordinates(endX, endY);
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipeSequence = new Sequence(finger, 1);
            swipeSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipeSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipeSequence.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), endX, endY));
            swipeSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(swipeSequence));
            System.out.println("Swipe performed from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");
        } catch (Exception e) {
            handleException(e, "swiping from point to point");
        }
    }

    public static void doubleTap(int x, int y) {
        try {
            validateCoordinates(x, y);
            executeTap(x, y, Duration.ofMillis(50));
            executeTap(x, y, Duration.ofMillis(50));
        } catch (Exception e) {
            handleException(e, "double tapping at coordinates");
        }
    }

    public static void pinch(int centerX, int centerY, int distance, Duration duration) {
        try {
            validateCoordinates(centerX, centerY);
            multiTouch(centerX, centerY, centerX, centerY - distance, centerX, centerY + distance, duration);
            System.out.println("Pinch performed at center (" + centerX + "," + centerY + ")");
        } catch (Exception e) {
            handleException(e, "pinching at coordinates");
        }
    }

    public static void zoom(int centerX, int centerY, int distance, Duration duration) {
        try {
            validateCoordinates(centerX, centerY);
            multiTouch(centerX, centerY, centerX, centerY, centerX, centerY, duration);
            System.out.println("Zoom performed at center (" + centerX + "," + centerY + ")");
        } catch (Exception e) {
            handleException(e, "zooming at coordinates");
        }
    }

    public static void drag(int startX, int startY, int endX, int endY, Duration duration) {
        try {
            validateCoordinates(startX, startY);
            validateCoordinates(endX, endY);
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence dragSequence = new Sequence(finger, 1);
            dragSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            dragSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            dragSequence.addAction(new Pause(finger, Duration.ofMillis(200)));
            dragSequence.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), endX, endY));
            dragSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(dragSequence));
            System.out.println("Drag performed from (" + startX + "," + startY + ") to (" + endX + "," + endY + ")");
        } catch (Exception e) {
            handleException(e, "dragging from point to point");
        }
    }

    private static void executeTap(int x, int y, Duration duration) {
        validateCoordinates(x, y);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapSequence = new Sequence(finger, 1);
        tapSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tapSequence.addAction(new Pause(finger, duration));
        tapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(tapSequence));
        System.out.println("Tap performed at coordinates: " + x + ", " + y);
    }

    private static void multiTouch(int centerX, int centerY, int startX1, int startY1, int startX2, int startY2, Duration duration) {
        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");
        Sequence multiTouchSequence1 = new Sequence(finger1, 1);
        Sequence multiTouchSequence2 = new Sequence(finger2, 1);
        multiTouchSequence1.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX1, startY1));
        multiTouchSequence2.addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX2, startY2));
        multiTouchSequence1.addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        multiTouchSequence2.addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        multiTouchSequence1.addAction(finger1.createPointerMove(duration, PointerInput.Origin.viewport(), centerX, centerY));
        multiTouchSequence2.addAction(finger2.createPointerMove(duration, PointerInput.Origin.viewport(), centerX, centerY));
        multiTouchSequence1.addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        multiTouchSequence2.addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(multiTouchSequence1, multiTouchSequence2));
    }

    private static void handleException(Exception e, String action) {
        System.out.println("Error while " + action + ": " + e.getMessage());
        e.printStackTrace();
    }
}
//Tap: 100ms đến 150ms.
//Long Press: 500ms đến 800ms.
//Swipe: 400ms đến 600ms.
//Double Tap: 150ms đến 250ms (giữa hai lần chạm).
//Delay giữa các hành động: 500ms đến 1 giây.
//Tính ngẫu nhiên: Thêm khoảng thời gian ngẫu nhiên giữa các thao tác.
import java.io.IOException;
import java.util.logging.*;

public class DriverLogger {
    private static Logger logger;
    private static final String loggerName = "telegram-mobile-auto";

    public static Logger getLogger(){
        if(logger == null) {
            logger = Logger.getLogger(loggerName);
            setupLogger();
        }
        return logger;
    }

    private static void setupLogger() {
        try {
            // Thiết lập ConsoleHandler để ghi log ra console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

            // Thiết lập FileHandler để ghi log vào file
            FileHandler fileHandler = new FileHandler("logs/logger.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);

            logger.setLevel(Level.ALL);  // Ghi tất cả các log
            logger.setUseParentHandlers(false);  // Tắt log mặc định để tránh in lặp lại

        } catch (IOException e) {
            System.err.println("Error setting up logger: " + e.getMessage());
        }
    }
}

public class Data {
    //User config here
    private static final Integer moonbixIndex = Integer.parseInt(EnvConfig.getDotEnv().get("moonbix_chat_index"));
    public static final String deviceId = EnvConfig.getDotEnv().get("device_UID"); //RFCW502F4BR || emulator-5554 || emulator-5556
    public static final Integer delayTap = Integer.parseInt(EnvConfig.getDotEnv().get("delay_milliseconds"));

    //Telegram package
    public static final String appActivity = ".DefaultIcon";
    public static final String appPackage = "org.telegram.messenger";

    //Go to game
    public static String chatXpath = "//androidx.recyclerview.widget.RecyclerView/android.view.ViewGroup[" + moonbixIndex + "]"; //need to change index
    public static final String startGameButtonId = "Bot menu";
    public static final String backButtonXpath = "(//android.widget.Image)[1]";

    //Roll-Call
    public static final String yourDailyRecordXpath = "//android.widget.TextView[@text='Your Daily Record']";
    public static final String continueButtonXpath = "//android.widget.TextView[@text='Continue']";

    //Home
    public static final String myRecordsXpath = "//android.widget.TextView[@text=\"My Records\"]";
    public static final String PlayGameButtonXpath = "//android.widget.Button[contains(@text, \"Play Game (Your Attempt:\")]";
    public static final String surpriseTitleXpath = "//android.widget.TextView[@text=\"SURPRISE\"]";
    public static final String gameWidgetXpath = "//android.widget.TextView[@text=\"Game\"]";
    public static final String leaderboardWidgetXpath = "//android.widget.TextView[@text=\"Leaderboard\"]";
    public static final String taskWidgetXpath = "//android.widget.TextView[@text=\"Tasks\"]";
    public static final String friendsWidgetXpath = "//android.widget.TextView[@text=\"Friends\"]";
    public static final String surpriseWidgetXpath = "//android.widget.TextView[@text=\"Surprise\"]";
    public static final String inviteFriendXpath = "//android.widget.Button[@text=\"Invite Friends for Bonuses\"]";

    //Post game
    public static final String playAgainXpath = "//android.widget.Button[contains(@text, \"Play Again\")]"; //(//android.widget.Button)[1]
    public static final String shareWithFiends = "//android.widget.Button[@text=\"Share with Friends\"]"; //(//android.widget.Button)[1]
    public static final String shareWithFriendsXpath = "//android.widget.Button[@text=\"Share with Friends\"]"; //(//android.widget.Button)[2]
    public static final String continueXpath = "//android.widget.Button[@text=\"Continue\"]"; //(//android.widget.Button)[2]
    public static final String backToHomeXpath = "//android.view.View[@resource-id=\"__APP\"]/android.view.View/android.view.View/android.view.View/android.widget.Image";
    public static final String scoreXpath = "//android.widget.Image[@text='token']/preceding-sibling::android.widget.TextView";

    //common
    public static final String goBackXpath = "(//android.widget.ImageView[@content-desc=\"Go back\"])[1]"; //there are 2 buttons, 2nd one is back from chat, 1st is back from the game

    //Tasks page
    public static final String unfinishedTasksListXpath = "//android.view.View[@resource-id=\"__APP\"]/android.view.View/android.view.View[1]/android.view.View[not(android.widget.Image[@text='check'])]";

    //loading scene
    public static final String purpleStar = "//android.widget.Image[@text=\"star-purple\"]";
    public static final String blueStar = "//android.widget.Image[@text=\"star-blue\"]";
    public static final String marsIcon = "//android.widget.Image[@text=\"loading-kv\"]";
    public static final String percentLoading = "//android.widget.TextView[contains(@text, '%')]";

    //In-game
    public static final String timerIcon = "//android.widget.Image[@text=\"icon-timer\"]";
}

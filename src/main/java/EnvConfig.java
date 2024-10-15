import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static Dotenv dotEnv;

    public static Dotenv getDotEnv(){
        if(dotEnv==null) {
            dotEnv = Dotenv.configure()
                    .directory("./")
                    .filename("local.env")
                    .load();
        }
        return dotEnv;
    }
}

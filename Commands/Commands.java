package Commands;

import java.util.List;

public class Commands {
    public static final String START = "/start";
    public static final String JOKE = "/joke";
    public static final String TOP = "/top";
    public static final String HELP = "/help";
    public static final String REG = "/reg";
    public static final String ADD = "/add";
    public static final String MINE = "/mine";
    public static final String DELETE = "/delete";
    public static final String ADD_ERROR = "ADD_ERROR_4dqw8d21dqwd2D@qfwfewgegwegwsdSX18623114281WD";

    private static final List<String> commandsList = List.of(
            "/start",
            "/joke",
            "/top",
            "/help",
            "/reg",
            "/add",
            "/mine",
            "/delete"
    );

    public static boolean isCommand(String text) {
        boolean isCommand = false;
        for (String command : commandsList) {
            if (text.equals(command)) {
                isCommand = true;
                break;
            }
        }
        return isCommand;
    }
}
/*
        Commands
help - Get command descriptions
joke - Get random joke
top - Get top jokes
reg -  Register my account
add - Add my joke
mine - Show all my jokes
delete - Delete my account
 */
package ui;

/**
 * Class for storing the syntax of different ChessClient commands.
 */
public class CommandSyntax {

    public static final String REGISTER = "register <USERNAME> <PASSWORD> <EMAIL>";
    public static final String LOGIN = "login <USERNAME> <PASSWORD>";
    public static final String QUIT = "quit";
    public static final String HELP = "help";

    public static final String CREATE = "create <NAME>";
    public static final String LIST = "list";
    public static final String JOIN = "join <ID> [WHITE|BLACK]";
    public static final String OBSERVE = "observe <ID>";
    public static final String LOGOUT = "logout";

    public static final String EXIT = "exit";
}

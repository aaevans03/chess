package ui;

import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

/**
 * Class for storing the syntax of different ChessClient commands, as well as the help command.
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

    public static final String REDRAW = "redraw";
    public static final String MAKE_MOVE = "move [a-h][1-8] [a-h][1-8]";
    public static final String HIGHLIGHT_LEGAL_MOVES = "highlight OR moves [a-h][1-8]";
    public static final String RESIGN = "resign";
    public static final String EXIT = "exit";

    public static String help(ClientState clientState) {
        var output = new StringBuilder();

        switch (clientState) {
            case PRE_LOGIN -> {
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.REGISTER);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - create a new account\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.LOGIN);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - login with an existing account\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.QUIT);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - quit the program\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.HELP);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - display list of commands\n");
            }
            case POST_LOGIN -> {
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.CREATE);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - create a new chess game with specified name\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.LIST);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - view list of games\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.JOIN);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - join a game as specified color\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.OBSERVE);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - observe a game\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.LOGOUT);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - log out of chess\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.QUIT);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - quit the program\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.HELP);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - display list of commands\n");
            }
            case GAMEPLAY -> {
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.REDRAW);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - redraw chess board on screen\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.MAKE_MOVE);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - move a chess piece\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.HIGHLIGHT_LEGAL_MOVES);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - highlight all legal moves of a piece\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.RESIGN);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - resign and end the game\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.EXIT);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - exit current game\n");
                output.append(SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.HELP);
                output.append(SET_TEXT_COLOR_LIGHT_GREY + " - display list of commands\n");
            }
        }

        return output.toString();
    }
}

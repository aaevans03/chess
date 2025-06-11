package websocket.messages;

public class ErrorMessage extends ServerMessage {

    String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = "Error: " + errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

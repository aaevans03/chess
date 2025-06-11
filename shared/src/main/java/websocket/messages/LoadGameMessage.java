package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    ChessGame game;
    boolean isEnded;

    public LoadGameMessage(ChessGame game, boolean isEnded) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.isEnded = isEnded;
    }

    public ChessGame getGame() {
        return game;
    }

    public boolean isEnded() {
        return isEnded;
    }
}

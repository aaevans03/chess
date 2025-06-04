package serverfacade.request;

import chess.ChessGame;
import com.google.gson.annotations.Expose;

public record JoinRequest(String authToken, @Expose ChessGame.TeamColor playerColor, @Expose int gameID) {
}

package service.request;

import chess.ChessGame;

public record JoinRequest(String authToken, ChessGame.TeamColor playerColor, int gameID) {
}

package websocket.messages;

public enum NotificationType {
    PLAYER_JOIN_WHITE,
    PLAYER_JOIN_BLACK,
    OBSERVER_JOIN,
    MOVE_MADE,
    LEAVE_GAME,
    RESIGN,
    CHECK,
    CHECKMATE,
    STALEMATE
}

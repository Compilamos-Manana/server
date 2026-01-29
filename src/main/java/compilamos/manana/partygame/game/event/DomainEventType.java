package compilamos.manana.partygame.game.event;

public enum DomainEventType {

    // LOBBY / ROOM
    PLAYER_JOINED,
    PLAYER_LEFT,
    PLAYER_DISCONNECTED,
    PLAYER_CONNECTED,
    HOST_CONNECTED,
    HOST_DISCONNECTED,
    HOST_SNAPSHOT,
    PLAYER_SNAPSHOT,

    // GAME
    GAME_STARTED,
    NEW_ROUND_STARTED,
    QUESTION_ASSIGNED
}

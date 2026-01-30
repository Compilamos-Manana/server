package compilamos.manana.partygame.game.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class EventMetadata {
    private final String roomCode;
    private final String gameId;
    private final String playerId;
    private final int cycleNumber;

    public EventMetadata(String roomCode, String gameId, int cycleNumber) {
        this.roomCode = roomCode;
        this.gameId = gameId;
        this.cycleNumber = cycleNumber;
        this.playerId = null;
    }

    public EventMetadata(String roomCode, String gameId, String playerId, int cycleNumber) {
        this.roomCode = roomCode;
        this.gameId = gameId;
        this.playerId = playerId;
        this.cycleNumber = cycleNumber;
    }
}

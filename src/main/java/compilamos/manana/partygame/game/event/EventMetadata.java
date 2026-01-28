package compilamos.manana.partygame.game.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class EventMetadata {
    private final String roomCode;
    private final String gameId;
    private final int cycleNumber;
}

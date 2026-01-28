package compilamos.manana.partygame.rooms.store;


import compilamos.manana.partygame.game.engine.GameEngine;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;


@Data
@AllArgsConstructor
public class RoomEntry {
    private String roomCode;
    private String gameId;
    private GameEngine gameEngine;
    private Instant createdAt;
    private Instant lastTocuhedAt;

    private boolean ended = false;
}

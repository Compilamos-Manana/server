package compilamos.manana.partygame.game.model.snapshot;

import compilamos.manana.partygame.game.model.ConnectionState;
import compilamos.manana.partygame.game.model.GameState;

import java.util.List;

public record HostSnapshot(
        String roomCode,
        String gameId,
        GameState gameState,
        ConnectionState hostConnectionState,
        int roundNumber,
        int cycleNumber,
        List<PlayerSnapshot> players,
        PlayerSnapshot impostor,
        String playerQuestion,
        String impostorQuestion
) {
}

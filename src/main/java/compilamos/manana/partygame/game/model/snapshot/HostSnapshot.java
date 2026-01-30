package compilamos.manana.partygame.game.model.snapshot;

import compilamos.manana.partygame.game.model.ConnectionState;
import compilamos.manana.partygame.game.model.GameState;
import compilamos.manana.partygame.game.model.question.Question;

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
        Question playerQuestion,
        Question impostorQuestion
) {
}

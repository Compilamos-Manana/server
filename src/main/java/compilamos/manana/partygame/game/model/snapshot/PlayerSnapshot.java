package compilamos.manana.partygame.game.model.snapshot;

import compilamos.manana.partygame.game.model.ConnectionState;
import compilamos.manana.partygame.game.model.PlayerState;

public record PlayerSnapshot(
        String playerId,
        String name,
        int avatarId,
        Boolean isImpostor,
        PlayerState state,
        ConnectionState connectionState
) {
}

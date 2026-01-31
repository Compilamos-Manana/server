package compilamos.manana.partygame.game.model.snapshot;

import compilamos.manana.partygame.game.model.ConnectionState;
import compilamos.manana.partygame.game.model.PlayerState;
import compilamos.manana.partygame.game.model.VoteOption;
import compilamos.manana.partygame.game.model.question.Question;

import java.util.List;

public record PlayerSnapshot(
        String playerId,
        String name,
        int avatarId,
        boolean isImpostor,
        PlayerState state,
        ConnectionState connectionState,
        Question currentQuestion,
        List<VoteOption> voteOptions
) {
}

package compilamos.manana.partygame.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vote {
    private String playerId;
    private String playerName;
    private String votedPlayerId;
    private String votedPlayerName;
}

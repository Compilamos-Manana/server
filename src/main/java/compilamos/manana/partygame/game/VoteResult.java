package compilamos.manana.partygame.game;

import compilamos.manana.partygame.game.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteResult {
    private String playerIdVoted;
    private String playerNameVoted;
    private String playerIdImpostor;
    private String playerNameImpostor;
}

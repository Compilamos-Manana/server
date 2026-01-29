package compilamos.manana.partygame.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {
    private String playerId;
    private String name;
    private int avatarId;
    private Boolean isImpostor;
    private PlayerState state;
    private ConnectionState connectionState;
    private String currentQuestion;
}

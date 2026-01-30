package compilamos.manana.partygame.game.model;

import compilamos.manana.partygame.game.model.question.Question;
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
    private boolean isImpostor;
    private PlayerState state;
    private ConnectionState connectionState;
    private Question currentQuestion;
}

package compilamos.manana.partygame.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@Builder
public class GameContext {
    private String roomCode;
    private String gameId;
    private ConnectionState hostConnectionState;
    private GameState gameState;
    private int roundNumber;
    private int cycleNumber;
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private Player impostorPlayer;
    private String playersQuestion;
    private String impostorQuestion;


    public void incrementCycleNumber() {
        this.cycleNumber++;
    }

    public void incrementRoundNumber() {
        this.roundNumber++;
    }
}

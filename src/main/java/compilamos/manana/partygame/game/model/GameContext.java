package compilamos.manana.partygame.game.model;

import compilamos.manana.partygame.game.model.question.Question;
import compilamos.manana.partygame.game.model.question.RoundQuestions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@Builder
public class GameContext {
    private String roomCode;
    private String gameId;
    private String questionSetName;
    private ConnectionState hostConnectionState;
    private GameState gameState;
    private int roundNumber;
    private int cycleNumber;
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final List<RoundQuestions> roundsQuestionsHistory = new ArrayList<>();
    private final List<Answer> currentRoundAnswers = new ArrayList<>();
    private final List<List<Answer>> roundsAnswersHistory = new ArrayList<>();
    private Question playersQuestion;
    private Question impostorQuestion;


    public void incrementCycleNumber() {
        this.cycleNumber++;
    }

    public void incrementRoundNumber() {
        this.roundNumber++;
    }
}

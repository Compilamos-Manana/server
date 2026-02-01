package compilamos.manana.partygame.game.model.snapshot;

import compilamos.manana.partygame.game.model.Answer;
import compilamos.manana.partygame.game.model.ConnectionState;
import compilamos.manana.partygame.game.model.GameState;
import compilamos.manana.partygame.game.model.Vote;
import compilamos.manana.partygame.game.model.question.Question;
import compilamos.manana.partygame.game.model.question.RoundQuestions;

import java.util.List;

public record HostSnapshot(
        String roomCode,
        String gameId,
        String questionSetName,
        GameState gameState,
        ConnectionState hostConnectionState,
        int roundNumber,
        int cycleNumber,
        List<PlayerSnapshot> players,
        PlayerSnapshot impostor,
        Question playerQuestion,
        Question impostorQuestion,
        List<RoundQuestions> roundsQuestionsHistory,
        List<Answer> currentRoundAnswers,
        List<List<Answer>> roundsAnswersHistory,
        List<Vote> currentRoundVotes,
        List<List<Vote>> roundsVotesHistory
) {
}

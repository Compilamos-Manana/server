package compilamos.manana.partygame.game.command;

public record SendAnswerCommand(
        String roomCode,
        String playerId,
        String answerText
) implements Command {
}

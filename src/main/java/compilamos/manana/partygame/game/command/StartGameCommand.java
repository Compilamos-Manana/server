package compilamos.manana.partygame.game.command;


public record StartGameCommand(
        String roomCode,
        String conjunto,
        int maxRounds
) implements Command {
}
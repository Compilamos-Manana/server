package compilamos.manana.partygame.game.command;

public record ProcessRoundCommand (
        String roomCode
) implements Command {
}

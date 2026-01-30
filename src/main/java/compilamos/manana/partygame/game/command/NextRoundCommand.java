package compilamos.manana.partygame.game.command;

public record NextRoundCommand(
        String roomCode
) implements Command {
}

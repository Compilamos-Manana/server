package compilamos.manana.partygame.game.command;

public record CustomEventCommand(
        String roomCode,
        Object payload
) implements Command {
}

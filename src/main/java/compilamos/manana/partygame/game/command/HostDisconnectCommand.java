package compilamos.manana.partygame.game.command;


public record HostDisconnectCommand(
        String roomCode
) implements Command {
}
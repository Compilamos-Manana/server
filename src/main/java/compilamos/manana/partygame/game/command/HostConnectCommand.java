package compilamos.manana.partygame.game.command;


public record HostConnectCommand(
        String roomCode
) implements Command {
}
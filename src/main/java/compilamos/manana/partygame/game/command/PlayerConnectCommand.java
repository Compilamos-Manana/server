package compilamos.manana.partygame.game.command;


public record PlayerConnectCommand(
        String roomCode,
        String playerId
) implements Command {
}
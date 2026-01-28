package compilamos.manana.partygame.game.command;


public record PlayerDisconnectCommand(
        String roomCode,
        String playerId
) implements Command {
}
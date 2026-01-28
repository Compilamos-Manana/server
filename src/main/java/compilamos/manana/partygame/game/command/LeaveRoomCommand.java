package compilamos.manana.partygame.game.command;


public record LeaveRoomCommand(
        String roomCode,
        String playerId
) implements Command {
}
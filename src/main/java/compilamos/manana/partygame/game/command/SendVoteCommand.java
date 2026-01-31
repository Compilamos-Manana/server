package compilamos.manana.partygame.game.command;

public record SendVoteCommand(
        String roomCode,
        String playerId,
        String votedPlayerId
) implements Command {
}

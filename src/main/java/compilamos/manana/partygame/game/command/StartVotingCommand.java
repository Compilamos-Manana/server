package compilamos.manana.partygame.game.command;

public record StartVotingCommand(
        String roomCode
) implements Command {
}

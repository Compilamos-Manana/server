package compilamos.manana.partygame.game.command;


import compilamos.manana.partygame.game.model.Player;

public record JoinRoomCommand(
        String roomCode,
        Player player
) implements Command {
}
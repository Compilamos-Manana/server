package compilamos.manana.partygame.game.event;


import compilamos.manana.partygame.game.model.GameContext;
import compilamos.manana.partygame.game.model.Player;
import compilamos.manana.partygame.game.model.snapshot.HostSnapshot;
import compilamos.manana.partygame.game.model.snapshot.PlayerSnapshot;

public final class EventBuilder {

    public static DomainEvent playerJoined(GameContext ctx, Player player) {
        return new DomainEvent(
                DomainEventType.PLAYER_JOINED,
                metadata(ctx),
                player
        );
    }

    public static DomainEvent playerLeft(GameContext ctx, Player player) {
        return new DomainEvent(
                DomainEventType.PLAYER_LEFT,
                metadata(ctx),
                player
        );
    }

    public static DomainEvent playerDisconnected(GameContext ctx, Player player) {
        return new DomainEvent(
                DomainEventType.PLAYER_DISCONNECTED,
                metadata(ctx),
                player
        );
    }

    public static DomainEvent playerConnected(GameContext ctx, Player player) {
        return new DomainEvent(
                DomainEventType.PLAYER_CONNECTED,
                metadata(ctx),
                player
        );
    }

    public static DomainEvent hostConnected(GameContext ctx) {
        return new DomainEvent(
                DomainEventType.HOST_CONNECTED,
                metadata(ctx),
                null
        );
    }

    public static DomainEvent hostDisconnected(GameContext ctx) {
        return new DomainEvent(
                DomainEventType.HOST_DISCONNECTED,
                metadata(ctx),
                null
        );
    }

    public static DomainEvent hostSnapshot(HostSnapshot snapshot) {
        return new DomainEvent(
                DomainEventType.HOST_SNAPSHOT,
                new EventMetadata(
                        snapshot.roomCode(),
                        snapshot.gameId(),
                        snapshot.cycleNumber()),
                snapshot
        );
    }

    public static DomainEvent playerSnapshot(GameContext ctx, PlayerSnapshot snapshot) {
        return new DomainEvent(
                DomainEventType.PLAYER_SNAPSHOT,
                metadata(ctx),
                snapshot
        );
    }

    private static EventMetadata metadata(GameContext ctx) {
        return new EventMetadata(
                ctx.getRoomCode(),
                ctx.getGameId(),
                ctx.getCycleNumber()
        );
    }

}

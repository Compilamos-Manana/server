package compilamos.manana.partygame.game.event;


import compilamos.manana.partygame.game.model.Answer;
import compilamos.manana.partygame.game.model.GameContext;
import compilamos.manana.partygame.game.model.Player;
import compilamos.manana.partygame.game.model.snapshot.HostSnapshot;
import compilamos.manana.partygame.game.model.snapshot.PlayerSnapshot;

public final class EventBuilder {

    public static DomainEvent respuestaEnviada(GameContext gameContext, Player player, Answer answer) {
        return new DomainEvent(
                DomainEventType.RESPUESTA_ENVIADA,
                metadata(gameContext, player),
                answer
        );
    }

    public static DomainEvent preguntaAsignada(GameContext gameContext, Player player) {
        return new DomainEvent(
                DomainEventType.PREGUNTA_ASIGNADA,
                metadata(gameContext, player),
                player
        );
    }

    public static DomainEvent rolesAsignados(GameContext gameContext, Player player) {
        return new DomainEvent(
                DomainEventType.ROLES_ASIGNADOS,
                metadata(gameContext, player),
                player
        );
    }
    public static DomainEvent nuevaRondaIniciada(GameContext gameContext) {
        return new DomainEvent(
                DomainEventType.RONDA_INICIADA,
                metadata(gameContext),
                null
        );
    }


    public static DomainEvent partidaIniciada(GameContext ctx) {
        return new DomainEvent(
                DomainEventType.PARTIDA_INICIADA,
                metadata(ctx),
                null
        );
    }

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

    public static DomainEvent customEvent(GameContext ctx, Object payload) {
        return new DomainEvent(
                DomainEventType.FRONTEND_CUSTOM_EVENT,
                metadata(ctx),
                payload
        );
    }

    private static EventMetadata metadata(GameContext ctx) {
        return new EventMetadata(
                ctx.getRoomCode(),
                ctx.getGameId(),
                ctx.getCycleNumber()
        );
    }

    private static EventMetadata metadata(GameContext ctx, Player player) {
        return new EventMetadata(
                ctx.getRoomCode(),
                ctx.getGameId(),
                player.getPlayerId(),
                ctx.getCycleNumber()
        );
    }

}

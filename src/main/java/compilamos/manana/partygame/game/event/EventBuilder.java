package compilamos.manana.partygame.game.event;


import compilamos.manana.partygame.api.exception.ApiException;
import compilamos.manana.partygame.api.exception.ErrorCode;
import compilamos.manana.partygame.game.VoteResult;
import compilamos.manana.partygame.game.model.*;
import compilamos.manana.partygame.game.model.snapshot.HostSnapshot;
import compilamos.manana.partygame.game.model.snapshot.PlayerSnapshot;
import org.springframework.http.HttpStatus;

import java.util.List;

public final class EventBuilder {

    public static DomainEvent perdedor(GameContext context, Player perdedor) {
        return new DomainEvent(
                DomainEventType.PERDISTE,
                metadata(context, perdedor),
                perdedor
        );
    }

    public static DomainEvent ganador(GameContext context, Player ganador) {
        return new DomainEvent(
                DomainEventType.GANASTE,
                metadata(context, ganador),
                ganador
        );
    }

    public static DomainEvent gananJugadores(GameContext context, Player impostor) {
        return new DomainEvent(
                DomainEventType.GANAN_JUGADORES,
                metadata(context),
                VoteResult.builder()
                        .playerIdImpostor(impostor.getPlayerId())
                        .playerNameImpostor(impostor.getName())
                        .playerIdVoted(impostor.getPlayerId())
                        .playerNameVoted(impostor.getName())
                        .build()
        );
    }

    public static DomainEvent ganaImpostor(GameContext context, Player impostor, Player votado) {
        if (impostor == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "Impostor player not found in context", HttpStatus.NOT_FOUND);
        }
        var playerIdVoted = votado != null ? votado.getPlayerId() : "";
        var playerNameVoted = votado != null ? votado.getName() : "";

        return new DomainEvent(
                DomainEventType.GANA_IMPOSTOR,
                metadata(context),
                VoteResult.builder()
                        .playerIdImpostor(impostor.getPlayerId())
                        .playerNameImpostor(impostor.getName())
                        .playerIdVoted(playerIdVoted)
                        .playerNameVoted(playerNameVoted)
                        .build()
        );
    }

    public static DomainEvent empateDeclarado(GameContext context) {
        context.setGameState( GameState.EMPATE);
        return new DomainEvent(
                DomainEventType.EMPATE,
                metadata(context),
                null
        );
    }

    public static DomainEvent votoEnviado(GameContext context, Player player, Vote vote) {
        return new DomainEvent(
                DomainEventType.VOTO_ENVIADO,
                metadata(context, player),
                vote
        );
    }

    public static DomainEvent votacionIniciada(GameContext gameContext, List<VoteOption> voteOptions) {
        return new DomainEvent(
                DomainEventType.VOTACION_INICIADA,
                metadata(gameContext),
                voteOptions
        );
    }

    public static DomainEvent debateIniciado(GameContext gameContext, List<Answer> answers) {
        return new DomainEvent(
                DomainEventType.DEBATE_INICIADO,
                metadata(gameContext),
                answers
        );
    }

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

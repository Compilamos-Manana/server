package compilamos.manana.partygame.sse.listener;


import compilamos.manana.partygame.game.event.DomainEvent;
import compilamos.manana.partygame.game.event.DomainEventType;
import compilamos.manana.partygame.game.model.Player;
import compilamos.manana.partygame.sse.registry.RoomEmitters;
import compilamos.manana.partygame.sse.registry.SseRegistry;
import compilamos.manana.partygame.sse.routing.EventRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
public class DomainEventSseListener {

    private final SseRegistry sseRegistry;
    private final EventRouter eventRouter;

    @Autowired
    public DomainEventSseListener(SseRegistry sseRegistry, EventRouter eventRouter) {
        this.sseRegistry = sseRegistry;
        this.eventRouter = eventRouter;
    }

    @EventListener
    public void onDomainEvent(DomainEvent event) {
        log.info("Domain Event Received: {}", event);

        String roomCode = event.metadata().getRoomCode();

        SseEmitter.SseEventBuilder sseEvent = SseEmitter.event()
                .name(event.type().name())
                .data(event);

        EventRouter.Audience audience = eventRouter.route(event);

        RoomEmitters roomEmitters = sseRegistry.getRoomEmitters(roomCode);

        if (roomEmitters == null) {
            log.info("No Room Emitters for room code {}", roomCode);
            return; // No emitters registered for this room
        }

        switch (audience) {
            case HOST -> roomEmitters.sendToHost(roomCode, sseEvent);
            case PLAYERS -> roomEmitters.sendToPlayers(roomCode, sseEvent);
            case ALL -> {
                roomEmitters.sendToHost(roomCode, sseEvent);
                roomEmitters.sendToPlayers(roomCode, sseEvent);
            }
        }

        if (event.type().equals(DomainEventType.PLAYER_LEFT)) {
            Player player = (Player) event.payload();
            roomEmitters.disconnectPlayer(player.getPlayerId());
        }
    }
}

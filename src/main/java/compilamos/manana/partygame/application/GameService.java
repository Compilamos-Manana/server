package compilamos.manana.partygame.application;

import compilamos.manana.partygame.game.event.DomainEvent;
import compilamos.manana.partygame.game.event.EventBuilder;
import compilamos.manana.partygame.rooms.lifecycle.RoomLifeCycleService;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final RoomLifeCycleService roomLifeCycleService;
    private final EventPublisher eventPublisher;

    public GameService(RoomLifeCycleService roomLifeCycleService, EventPublisher eventPublisher) {
        this.roomLifeCycleService = roomLifeCycleService;
        this.eventPublisher = eventPublisher;
    }

    public DomainEvent getHostSnapshot(String roomCode) {
        return EventBuilder.hostSnapshot(roomLifeCycleService.getGameEngine(roomCode).getHostSnapshot());
    }

    public DomainEvent getPlayerSnapshot(String roomCode, String playerId) {
        return roomLifeCycleService.getGameEngine(roomCode).getPlayerSnapshot(playerId);
    }

}

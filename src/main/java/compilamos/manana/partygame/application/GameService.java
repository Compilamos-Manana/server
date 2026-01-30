package compilamos.manana.partygame.application;

import compilamos.manana.partygame.game.command.NextRoundCommand;
import compilamos.manana.partygame.game.command.StartGameCommand;
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

    public void nextRound(String roomCode) {
        var gameEngine = roomLifeCycleService.getGameEngine(roomCode);
        var command = new NextRoundCommand(roomCode);
        var events = gameEngine.handle(command);
        events.forEach(eventPublisher::publish);
    }

    public void startGame(String roomCode, String conjunto, int maxRounds) {
        var gameEngine = roomLifeCycleService.getGameEngine(roomCode);
        StartGameCommand command = new StartGameCommand(roomCode, conjunto, maxRounds);
        var events = gameEngine.handle(command);
        events.forEach(eventPublisher::publish);
    }

    public DomainEvent getHostSnapshot(String roomCode) {
        return EventBuilder.hostSnapshot(roomLifeCycleService.getGameEngine(roomCode).getHostSnapshot());
    }

    public DomainEvent getPlayerSnapshot(String roomCode, String playerId) {
        return roomLifeCycleService.getGameEngine(roomCode).getPlayerSnapshot(playerId);
    }

}

package compilamos.manana.partygame.application;

import compilamos.manana.partygame.game.command.*;
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

    public void processRound(String roomCode) {
        var gameEngine = roomLifeCycleService.getGameEngine(roomCode);
        var command = new ProcessRoundCommand(roomCode);
        var events = gameEngine.handle(command);
        events.forEach(eventPublisher::publish);
    }

    public void sendVote(String roomCode, String playerId, String votedPlayerId) {
        var gameEngine = roomLifeCycleService.getGameEngine(roomCode);

        var command = new SendVoteCommand(roomCode, playerId, votedPlayerId);
        var events = gameEngine.handle(command);

        events.forEach(eventPublisher::publish);
    }

    public void startVoting(String roomCode) {
        var gameEngine = roomLifeCycleService.getGameEngine(roomCode);
        var command = new StartVotingCommand(roomCode);
        var events = gameEngine.handle(command);
        events.forEach(eventPublisher::publish);
    }

    public void startDebate(String roomCode) {
        var gameEngine = roomLifeCycleService.getGameEngine(roomCode);
        var command = new StartDebateCommand(roomCode);
        var events = gameEngine.handle(command);
        events.forEach(eventPublisher::publish);
    }

    public void sendAnswer(String roomCode, String playerId, String answer) {
        var gameEngine = roomLifeCycleService.getGameEngine(roomCode);

        var command = new SendAnswerCommand(roomCode, playerId, answer);
        var events = gameEngine.handle(command);

        events.forEach(eventPublisher::publish);
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

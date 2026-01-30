package compilamos.manana.partygame.rooms.lifecycle;


import compilamos.manana.partygame.api.exception.ApiException;
import compilamos.manana.partygame.api.exception.ErrorCode;
import compilamos.manana.partygame.application.EventPublisher;
import compilamos.manana.partygame.game.command.*;
import compilamos.manana.partygame.game.engine.GameEngine;
import compilamos.manana.partygame.game.event.DomainEvent;
import compilamos.manana.partygame.game.event.DomainEventType;
import compilamos.manana.partygame.game.event.EventMetadata;
import compilamos.manana.partygame.game.model.Player;
import compilamos.manana.partygame.game.model.PlayerState;
import compilamos.manana.partygame.rooms.store.RoomEntry;
import compilamos.manana.partygame.rooms.store.RoomStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RoomLifeCycleService {
    private final RoomStore roomStore;
    private final EventPublisher eventPublisher;

    public RoomLifeCycleService(RoomStore roomStore, EventPublisher eventPublisher) {
        this.roomStore = roomStore;
        this.eventPublisher = eventPublisher;
    }

    public GameEngine getGameEngine(String roomCode) {
        log.info("RoomLifeCycleService::getGameEngine - roomCode: {}", roomCode);
        var roomEntry = roomStore.requireRoom(roomCode);
        return roomEntry.getGameEngine();
    }

    public RoomEntry createRoom() {
        log.info("RoomLifeCycleService::createRoom");
        return roomStore.createRoom();
    }

    public Player joinRoom(String roomCode, String playerName, int avatarId) {

        if (!roomCode.matches("\\d{4}")) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Room code must be a 4 digit number", HttpStatus.BAD_REQUEST);
        }

        if (playerName == null || playerName.trim().isEmpty() || playerName.length() > 20) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Player name must not be empty and max 20 characters", HttpStatus.BAD_REQUEST);
        }

        log.info("RoomLifeCycleService::joinRoom - roomCode: {}, playerName: {}, avatarId: {}", roomCode, playerName, avatarId);
        var roomEntry = roomStore.requireRoom(roomCode);
        String playerId = UUID.randomUUID().toString();
        Player newPlayer = Player.builder()
                .playerId(playerId)
                .name(playerName)
                .avatarId(avatarId)
                .state(PlayerState.IN_LOBBY)
                .build();
        JoinRoomCommand joinRoomCommand = new JoinRoomCommand(roomCode, newPlayer);

        List<DomainEvent> events = roomEntry.getGameEngine().handle(joinRoomCommand);

        eventPublisher.publishAll(events);
        return newPlayer;
    }

    public void leaveRoom(String roomCode, String playerId) {
        log.info("RoomLifeCycleService::leaveRoom - roomCode: {}, playerId: {}", roomCode, playerId);
        var roomEntry = roomStore.requireRoom(roomCode);
        LeaveRoomCommand leaveRoomCommand = new LeaveRoomCommand(roomCode, playerId);

        List<DomainEvent> events = roomEntry.getGameEngine().handle(leaveRoomCommand);

        eventPublisher.publishAll(events);
    }

    public void playerConnect(String roomCode, String playerId) {
        log.info("RoomLifeCycleService::playerConnect - roomCode: {}, playerId: {}", roomCode, playerId);

        var roomEntry = roomStore.requireRoom(roomCode);
        PlayerConnectCommand playerConnectCommand = new PlayerConnectCommand(roomCode, playerId);

        List<DomainEvent> events = roomEntry.getGameEngine().handle(playerConnectCommand);

        eventPublisher.publishAll(events);
    }

    public void playerDisconnect(String roomCode, String playerId) {
        log.info("RoomLifeCycleService::playerDisconnect - roomCode: {}, playerId: {}", roomCode, playerId);

        var roomEntry = roomStore.requireRoom(roomCode);

        PlayerDisconnectCommand playerDisconnectCommand = new PlayerDisconnectCommand(roomCode, playerId);

        List<DomainEvent> events = roomEntry.getGameEngine().handle(playerDisconnectCommand);

        eventPublisher.publishAll(events);
    }

    public void hostConnect(String roomCode) {
        log.info("RoomLifeCycleService::hostConnect - roomCode: {}", roomCode);

        var roomEntry = roomStore.requireRoom(roomCode);

        HostConnectCommand hostConnectCommand = new HostConnectCommand(roomCode);

        List<DomainEvent> events = roomEntry.getGameEngine().handle(hostConnectCommand);

        eventPublisher.publishAll(events);
    }

    public void hostDisconnect(String roomCode) {
        log.info("RoomLifeCycleService::hostDisconnect - roomCode: {}", roomCode);

        var roomEntry = roomStore.requireRoom(roomCode);

        HostDisconnectCommand hostDisconnectCommand = new HostDisconnectCommand(roomCode);

        List<DomainEvent> events = roomEntry.getGameEngine().handle(hostDisconnectCommand);

        eventPublisher.publishAll(events);
    }

    public void triggerCustomEvent(String roomCode, Object payload) {
        log.info("RoomLifeCycleService::triggerCustomEvent - roomCode: {}", roomCode);

        var roomEntry = roomStore.requireRoom(roomCode);

        CustomEventCommand customEventCommand = new CustomEventCommand(roomCode, payload);

        List<DomainEvent> events = roomEntry.getGameEngine().handle(customEventCommand);

        eventPublisher.publishAll(events);
    }
}

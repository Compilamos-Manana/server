package compilamos.manana.partygame.sse.controller;

import compilamos.manana.partygame.application.GameService;
import compilamos.manana.partygame.rooms.lifecycle.RoomLifeCycleService;
import compilamos.manana.partygame.sse.registry.SseRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse/player")
@Slf4j
public class PlayerSseController {
    private final SseRegistry sseRegistry;
    private final RoomLifeCycleService roomLifeCycleService;
    private final GameService gameService;

    @Autowired
    public PlayerSseController(SseRegistry sseRegistry, RoomLifeCycleService roomLifeCycleService, GameService gameService) {
        this.sseRegistry = sseRegistry;
        this.roomLifeCycleService = roomLifeCycleService;
        this.gameService = gameService;
    }

    @GetMapping
    public SseEmitter joinRoom(@RequestParam String roomCode, @RequestParam String playerId) {
        SseEmitter emmiter = new SseEmitter(0L); // Sin timeout
        roomLifeCycleService.playerConnect(roomCode, playerId);
        sseRegistry.registerPlayer(roomCode, playerId, emmiter);

        emmiter.onCompletion(() -> {
            log.info("Emmiter completed for player: " + playerId + " in room: " + roomCode);
            sseRegistry.removePlayer(roomCode, playerId);
            roomLifeCycleService.playerDisconnect(roomCode, playerId);
        });

        emmiter.onTimeout(() -> {
            log.error("Emmiter timeout for player: " + playerId + " in room: " + roomCode);
            emmiter.complete();
        });

        emmiter.onError((e) -> {
            log.error("Emmiter error for player: " + playerId + " in room: " + roomCode, e);
            emmiter.completeWithError(e);
        });

        sseRegistry.getRoomEmitters(roomCode).sentToPlayer(playerId,
                SseEmitter.event()
                        .name("PLAYER_SNAPSHOT")
                        .data(gameService.getPlayerSnapshot(roomCode, playerId))
        );

        return emmiter;
    }

    @GetMapping("/test")
    public void testSendMessageToPlayer(@RequestParam String roomCode, @RequestParam String message) {
        sseRegistry.getRoomEmitters(roomCode)
                .sendToPlayers(roomCode,
                        SseEmitter.event()
                                .name("test-message")
                                .data(message)
                );
    }
}

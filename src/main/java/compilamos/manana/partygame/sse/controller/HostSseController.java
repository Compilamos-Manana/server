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
@RequestMapping("/sse/host")
@Slf4j
public class HostSseController {
    private final SseRegistry sseRegistry;
    private final GameService gameService;
    private final RoomLifeCycleService roomLifeCycleService;

    @Autowired
    public HostSseController(SseRegistry sseRegistry, GameService gameService, RoomLifeCycleService roomLifeCycleService) {
        this.sseRegistry = sseRegistry;
        this.gameService = gameService;
        this.roomLifeCycleService = roomLifeCycleService;
    }

    @GetMapping()
    public SseEmitter createRoom(@RequestParam String roomName) {
        SseEmitter emitter = new SseEmitter(0L); // Sin timeout
        sseRegistry.registerHost(roomName, emitter);
        roomLifeCycleService.hostConnect(roomName);

        emitter.onCompletion(() -> {
            log.info("Emitter completed for room: " + roomName);
            sseRegistry.removeHost(roomName);
            roomLifeCycleService.hostDisconnect(roomName);

        });

        emitter.onTimeout(() -> {
            log.error("Emitter timeout for room: " + roomName);
            emitter.complete();
        });

        emitter.onError((e) -> {
            log.error("Emitter error for room: " + roomName, e);
            emitter.completeWithError(e);
        });

        sseRegistry.getRoomEmitters(roomName).sendToHost(roomName,
                SseEmitter.event()
                        .name("HOST_SNAPSHOT")
                        .data(gameService.getHostSnapshot(roomName))
        );
        return emitter;
    }

    @GetMapping("/test")
    public void testSendMessageToHost(@RequestParam String roomCode, @RequestParam String message) {
        sseRegistry.getRoomEmitters(roomCode)
                .sendToHost(roomCode,
                        SseEmitter.event()
                                .name("test-message")
                                .data(message)
                );
    }
}

package compilamos.manana.partygame.sse.registry;

import compilamos.manana.partygame.sse.model.EmitterRef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RoomEmitters {
    private volatile EmitterRef host;
    /**
     * key: playerId
     * value: SseEmitter
     */
    private final ConcurrentHashMap<String, EmitterRef> playerRefs = new ConcurrentHashMap<>();
    private final Duration keepAliveIfIdleFor = Duration.ofSeconds(10);

    /**
     * Constructor
     * @param hostEmitter the SseEmitter for the host
     */
    public RoomEmitters(SseEmitter hostEmitter) {
        this.host = new EmitterRef(hostEmitter);
        log.info("RoomEmitters created");
    }

    public boolean hasHost() {
        return this.host != null;
    }

    public void addHost(SseEmitter emitter) {
//        if (this.host != null) {
//            log.error("Host emitter already exists");
//            throw new IllegalArgumentException("Host emitter already exists");
//        }

        this.host = new EmitterRef(emitter);
        log.info("Host emitter added");
    }

    public void addPlayerEmitter(String playerId, SseEmitter emitter) {
//        if (playerRefs.containsKey(playerId)) {
//            log.error("Player emitter already exists: {}", playerId);
//            throw new IllegalArgumentException("Player emitter already exists");
//        }

        playerRefs.put(playerId, new EmitterRef(emitter));
        log.info("Player emitter added: {}", playerId);
    }

    public void removeHost() {
        this.host = null;
        log.info("Host emitter removed");
    }

    public void removePlayerEmitter(String playerId) {
        playerRefs.remove(playerId);
        log.info("Player emitter removed: {}", playerId);
    }

    public void disconnectPlayer(String playerId) {
        EmitterRef ref = playerRefs.remove(playerId);
        if (ref != null) {
            ref.getEmitter().complete();
            log.info("Player emitter disconnected: {}", playerId);
        }
    }

    /**
     * Helper de envío a host. En tu MVP puede quedar aquí mismo.
     */
    public void sendToHost(String roomCode, SseEmitter.SseEventBuilder event) {
        EmitterRef host = this.host;
        if (host == null) return;

        safeSend(roomCode, host, event, () -> {
            log.info("Host emitter dead for room: {}", roomCode);
            removeHost();
        });
    }


    /**
     * Helper de envío a todos los players.
     */
    public void sendToPlayers(String roomCode, SseEmitter.SseEventBuilder event) {
        for (Map.Entry<String, EmitterRef> entry : playerRefs.entrySet()) {
            String playerId = entry.getKey();
            EmitterRef ref = entry.getValue();

            safeSend(roomCode, ref, event, () -> {
                playerRefs.remove(playerId);
                log.info("Player emitter dead and removed: {} from room: {}", playerId, roomCode);
            });
        }
    }

    public void sentToPlayer(String playerId, SseEmitter.SseEventBuilder event) {
        EmitterRef ref = playerRefs.get(playerId);
        if (ref == null) return;

        safeSend("player " + playerId, ref, event, () -> {
            playerRefs.remove(playerId);
            log.info("Player emitter dead and removed: {}", playerId);
        });
    }

    public void sendKeepAliveAll() {
        long nowMs = System.currentTimeMillis();
        SseEmitter.SseEventBuilder keepAliveEvent = SseEmitter.event()
                .comment("keep-alive " + nowMs);

        // Host
        EmitterRef hostRef = this.host;
        if (hostRef != null) {
            long lastSentAt = hostRef.getLastSentAtMs().get();
            if (nowMs - lastSentAt >= keepAliveIfIdleFor.toMillis()) {
                safeSend("host", hostRef, keepAliveEvent, () -> {
                    log.info("Host emitter dead during keep-alive");
                    this.host = null;
                });
            }
        }

        // Players
        for (Map.Entry<String, EmitterRef> entry : playerRefs.entrySet()) {
            String playerId = entry.getKey();
            EmitterRef ref = entry.getValue();

            long lastSentAt = ref.getLastSentAtMs().get();
            if (nowMs - lastSentAt >= keepAliveIfIdleFor.toMillis()) {
                safeSend("player " + playerId, ref, keepAliveEvent, () -> {
                    playerRefs.remove(playerId);
                    log.info("Player emitter dead and removed during keep-alive: {}", playerId);
                });
            }
        }
    }

    private void safeSend(String roomCode,
                          EmitterRef ref,
                          SseEmitter.SseEventBuilder event,
                          Runnable onDeadEmitter) {
        try {
            // Nota: SseEmitter maneja content-type text/event-stream; no hace falta setear MediaType.
            ref.getEmitter().send(event);
            ref.getLastSentAtMs().set(System.currentTimeMillis());
        } catch (IOException | IllegalStateException ex) {
            // IOException: cliente se fue / conexión rota
            // IllegalStateException: emitter completado
            onDeadEmitter.run();
        }
    }

}

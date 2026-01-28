package compilamos.manana.partygame.sse.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Component
@Slf4j
public class SseRegistry {
    /*
    key: roomId
    value: RoomEmitters
     */
    Map<String, RoomEmitters> roomEmittersMap;

    public SseRegistry() {
        roomEmittersMap = new java.util.concurrent.ConcurrentHashMap<>();
        log.info("SseRegistry initialized");
    }

    /***
     * Register a host emitter for a room
     * @param roomCode the code of the room
     * @param emitter the SseEmitter for the host
     */
    public void registerHost(String roomCode, SseEmitter emitter) {
        RoomEmitters roomEmitters = roomEmittersMap.computeIfAbsent(roomCode, k -> new RoomEmitters(emitter));
        roomEmitters.addHost(emitter);
        roomEmittersMap.put(roomCode, roomEmitters);
        log.info("Host registered for room: {}", roomCode);
    }

    /***
     * Register a player emitter for a room
     * @param roomCode the code of the room
     * @param playerId the id of the player
     * @param emitter the SseEmitter for the player
     */
    public void registerPlayer(String roomCode, String playerId, SseEmitter emitter) {
        RoomEmitters roomEmitters = roomEmittersMap.get(roomCode);

        if (roomEmitters == null) {
            log.error("Room code not found: {}", roomCode);
            throw new IllegalArgumentException("Room code not found");
        }

        roomEmitters.addPlayerEmitter(playerId, emitter);
        log.info("Player {} registered for room: {}", playerId, roomCode);
    }

    /***
     * Remove the host emitter for a room
     * @param roomCode the code of the room
     */
    public void removeHost(String roomCode) {
        roomEmittersMap.get(roomCode).removeHost();
        log.info("Host removed for room: {}", roomCode);
    }

    public void removeRoom(String roomCode) {
        roomEmittersMap.remove(roomCode);
        log.info("Room removed: {}", roomCode);
    }

    /***
     * Remove a player emitter from a room
     * @param roomCode the code of the room
     * @param playerId the id of the player
     */
    public void removePlayer(String roomCode, String playerId) {
        RoomEmitters roomEmitters = roomEmittersMap.get(roomCode);

        if (roomEmitters == null) {
            log.error("Room code not found when removing player: {}", roomCode);
            throw new IllegalArgumentException("Room code not found");
        }

        roomEmitters.removePlayerEmitter(playerId);
        log.info("Player {} removed from room: {}", playerId, roomCode);
    }

    /***
     * Get the RoomEmitters for a room
     * @param roomCode the code of the room
     * @return the RoomEmitters object
     */
    public RoomEmitters getRoomEmitters(String roomCode) {
        return roomEmittersMap.get(roomCode);
    }

    public void sendKeepAliveAll() {
        for (RoomEmitters roomEmitters : roomEmittersMap.values()) {
            roomEmitters.sendKeepAliveAll();
        }
    }

}

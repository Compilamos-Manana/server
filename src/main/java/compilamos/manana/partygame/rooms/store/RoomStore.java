package compilamos.manana.partygame.rooms.store;


import compilamos.manana.partygame.api.exception.ApiException;
import compilamos.manana.partygame.api.exception.ErrorCode;
import compilamos.manana.partygame.config.GameConfig;
import compilamos.manana.partygame.game.engine.GameEngine;
import compilamos.manana.partygame.game.model.GameState;
import compilamos.manana.partygame.game.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RoomStore {
    private final int roomCodeLength = 4;
    private final GameConfig gameConfig;
    private final QuestionService questionService;

    /**
     * key: roomCode
     * value: RoomEntry
     */
    ConcurrentHashMap<String, RoomEntry> rooms = new ConcurrentHashMap<>();

    public RoomStore(GameConfig gameConfig, QuestionService questionService) {
        this.gameConfig = gameConfig;
        this.questionService = questionService;
    }

    public RoomEntry createRoom() {
        int roomCodeInt = generateRoomCode();
        String roomCodeStr = String.valueOf(roomCodeInt);
        String gameId = java.util.UUID.randomUUID().toString();
        int stateVersion = 0;
        GameState initialGameState = GameState.LOBBY;
        Instant now = Instant.now();

        RoomEntry roomEntry  = new RoomEntry(
                roomCodeStr,
                gameId,
                new GameEngine(roomCodeStr, gameId, gameConfig, questionService),
                now,
                now,
                false
        );
        rooms.put(roomCodeStr, roomEntry);

        return roomEntry;
    }

    public Optional<RoomEntry> getRoom(String roomCode) {
        return Optional.ofNullable(rooms.get(roomCode));
    }

    public RoomEntry requireRoom(String roomCode) {
        RoomEntry roomEntry = rooms.get(roomCode);
        touchRoom(roomCode);
        if (roomEntry == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Room code not found: " + roomCode, HttpStatus.NOT_FOUND);
        }
        return roomEntry;
    }

    public void touchRoom(String roomCode) {
        log.info("Touching room: {}", roomCode);
        RoomEntry roomEntry = requireRoom(roomCode);
        roomEntry.setLastTocuhedAt(Instant.now());
    }

    /**
     * Generate a unique room code. The room code is a numeric code with a fixed length. The room code generated must not already exist in the store.
     * @return
     */
    private int generateRoomCode() {
        int maxAttempts = 1000;
        int attempt = 0;
        int upperBound = (int) Math.pow(10, roomCodeLength);
        int lowerBound = (int) Math.pow(10, roomCodeLength - 1);

        while (attempt < maxAttempts) {
            int roomCode = (int) (Math.random() * (upperBound - lowerBound)) + lowerBound;
            String roomCodeStr = String.valueOf(roomCode);
            if (!rooms.containsKey(roomCodeStr)) {
                return roomCode;
            }
            attempt++;
        }

        throw new RuntimeException("Failed to generate unique room code after " + maxAttempts + " attempts");
    }

}

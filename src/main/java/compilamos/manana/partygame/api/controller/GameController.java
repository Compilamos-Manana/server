package compilamos.manana.partygame.api.controller;

import compilamos.manana.partygame.api.dto.request.SendAnswerRequest;
import compilamos.manana.partygame.api.dto.request.StartGameRequest;
import compilamos.manana.partygame.application.GameService;
import compilamos.manana.partygame.game.event.DomainEvent;
import compilamos.manana.partygame.game.model.snapshot.HostSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@Slf4j
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{roomCode}/send-answer")
    public ResponseEntity<Void> sendAnswer(@PathVariable String roomCode, @RequestBody SendAnswerRequest request) {
        log.info("Request to send answer for room: {}, player: {}", roomCode, request.getPlayerId());
        gameService.sendAnswer(roomCode, request.getPlayerId(), request.getAnswerText());
        return ResponseEntity.ok().build();
    }

                                           @PostMapping("/{roomCode}/next-round")
    public ResponseEntity<Void> nextRound(@PathVariable String roomCode) {
        log.info("Request to advance to next round for room: {}", roomCode);
        gameService.nextRound(roomCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomCode}/start")
    public ResponseEntity<Void> startGame(@PathVariable String roomCode, @RequestBody StartGameRequest startGameRequest) {
        log.info("Request to start game for room: {}", roomCode);
        gameService.startGame(roomCode, startGameRequest.getConjunto(), startGameRequest.getMaxRounds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomCode}/host/snapshot")
    public ResponseEntity<DomainEvent> getHostSnapshot(@PathVariable String roomCode) {
        log.info("Request to get host snapshot for room: {}", roomCode);
        var snapshot = gameService.getHostSnapshot(roomCode);
        return ResponseEntity.ok(snapshot);
    }

    @GetMapping("/{roomCode}/player/{playerId}/snapshot")
    public ResponseEntity<DomainEvent> getPlayerSnapshot(@PathVariable String roomCode, @PathVariable String playerId) {
        log.info("Request to get player snapshot for player: {} in room: {}", playerId, roomCode);
        var snapshot = gameService.getPlayerSnapshot(roomCode, playerId);
        return ResponseEntity.ok(snapshot);
    }

}

package compilamos.manana.partygame.api.controller;

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

    @PostMapping("/{roomCode}/start")
    public void startGame() {
        log.info("Request to start game received");
        // Lógica para iniciar el juego en la sala especificada por roomCode
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

package compilamos.manana.partygame.api.controller;

import compilamos.manana.partygame.rooms.lifecycle.RoomLifeCycleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/custom-event")
public class CustomEvent {
    private final RoomLifeCycleService roomLifeCycleService;

    public CustomEvent(RoomLifeCycleService roomLifeCycleService) {
        this.roomLifeCycleService = roomLifeCycleService;
    }

    @PostMapping
    @Operation(summary = "Trigger a custom event for frontend purposes. This event is not part of the game domain events and will not be change the game state. Therefore a game snapshot will not be generated with this event. Use with caution.")
    public void triggerCustomEvent(@RequestParam String roomCode, @RequestBody Object payload) {
        roomLifeCycleService.triggerCustomEvent(roomCode, payload);
    }
}

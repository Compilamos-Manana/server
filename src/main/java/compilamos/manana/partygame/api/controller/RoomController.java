package compilamos.manana.partygame.api.controller;


import compilamos.manana.partygame.api.dto.request.JoinRoomRequest;
import compilamos.manana.partygame.api.dto.request.LeavePlayerRequest;
import compilamos.manana.partygame.api.dto.response.JoinRoomResponse;
import compilamos.manana.partygame.rooms.lifecycle.RoomLifeCycleService;
import compilamos.manana.partygame.rooms.store.RoomEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
@Slf4j
public class RoomController {
    private final RoomLifeCycleService roomLifeCycleService;

    public RoomController(RoomLifeCycleService roomLifeCycleService) {
        this.roomLifeCycleService = roomLifeCycleService;
    }

    @PostMapping("/create")
    public ResponseEntity<RoomEntry> createRoom() {
        log.info("Request to create room received");
        RoomEntry roomEntry = roomLifeCycleService.createRoom();
        log.info("Room created with code: {}", roomEntry.getRoomCode());
        return ResponseEntity.ok(roomEntry);
    }

    @PostMapping("/join")
    public ResponseEntity<JoinRoomResponse> joinRoom(@RequestBody JoinRoomRequest joinRoomRequest) {
        log.info("Request to join room received");
        var player = roomLifeCycleService.joinRoom(joinRoomRequest.getRoomCode(),
                                                     joinRoomRequest.getName(),
                                                     joinRoomRequest.getAvatarId());
        return ResponseEntity.ok(new JoinRoomResponse(player.getPlayerId()));
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveRoom(@RequestBody LeavePlayerRequest leavePlayerRequest) {
        log.info("Request to leave player received");
        roomLifeCycleService.leaveRoom(leavePlayerRequest.getRoomCode(), leavePlayerRequest.getPlayerId());
        return ResponseEntity.ok().build();
    }
}

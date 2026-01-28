package compilamos.manana.partygame.api.dto.request;

import lombok.Data;

@Data
public class LeavePlayerRequest {
    private String roomCode;
    private String playerId;
}

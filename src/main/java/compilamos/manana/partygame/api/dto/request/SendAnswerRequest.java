package compilamos.manana.partygame.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendAnswerRequest {
    private String roomCode;
    private String playerId;
    private String answerText;
}

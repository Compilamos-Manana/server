package compilamos.manana.partygame.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartGameRequest {
    private String conjunto;
    private int maxRounds;
}

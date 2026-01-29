package compilamos.manana.partygame.game.model.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question {
    private int id;
    private String pregunta;
    private Map<String, String> extra_data;
}

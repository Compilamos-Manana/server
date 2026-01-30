package compilamos.manana.partygame.game.model.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionSet {
    private String id;
    private String conjunto;
    private List<RoundQuestions> preguntas;
    private String idioma;
    private String categoria;
}

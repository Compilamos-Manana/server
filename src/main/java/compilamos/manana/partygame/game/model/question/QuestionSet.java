package compilamos.manana.partygame.game.model.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection="question_sets")
public class QuestionSet {
    private String id;
    private String conjunto;
    private List<RoundQuestions> preguntas;
    private String idioma;
    private String categoria;
}

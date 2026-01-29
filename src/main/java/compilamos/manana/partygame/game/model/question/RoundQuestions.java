package compilamos.manana.partygame.game.model.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "round_questions")
public class RoundQuestions {
    private String id;
    private Map<String, String> extra_data;
    List<Question> preguntas;
    private String idioma;
    private Boolean nsfw;
    private String categoria;
}

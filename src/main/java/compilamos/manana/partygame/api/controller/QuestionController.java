package compilamos.manana.partygame.api.controller;

import compilamos.manana.partygame.game.model.question.Question;
import compilamos.manana.partygame.game.model.question.QuestionSet;
import compilamos.manana.partygame.game.model.question.RoundQuestions;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @GetMapping
    @Operation(summary = "Obtener los conjuntos de preguntas disponibles")
    public ResponseEntity<List<String>> getQuestionsSets() {
        // Lógica para obtener los conjuntos de preguntas
        return ResponseEntity.ok(List.of("Question Set 1", "Question Set 2"));
    }

    @GetMapping("/{setId}")
    @Operation(summary = "Obtener las preguntas de un conjunto específico")
    public ResponseEntity<QuestionSet> getQuestionsFromSet() {
        // Lógica para obtener las preguntas de un conjunto específico
        return ResponseEntity.ok(new QuestionSet());
    }

    @GetMapping("/{setId}/random")
    @Operation(summary = "Obtener un round questions aleatorio. Excluyendo ids ya usadas.")
    public ResponseEntity<RoundQuestions> getRandomQuestionFromSet(@RequestParam(value = "excludedIds", required = false) List<Integer> excludedIds) {
        // Lógica para obtener una pregunta aleatoria de un conjunto específico, excluyendo las IDs proporcionadas
        return ResponseEntity.ok(new RoundQuestions());
    }
}

package compilamos.manana.partygame.game.question.service;

import compilamos.manana.partygame.game.model.question.Question;
import compilamos.manana.partygame.game.model.question.RoundQuestions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    public RoundQuestions getRandomQuestion(List<String> excludedQuestions) {
        // Lógica para obtener una pregunta aleatoria que no esté en la lista de preguntas excluidas
        return RoundQuestions.builder()
                .id("random-question-id")
                .idioma("spanish")
                .nsfw(true)
                .categoria(null)
                .preguntas(
                        List.of(Question.builder()
                                .id(1)
                                .pregunta("¿Cuál es la capital de Francia?")
                                .build(),
                                Question.builder()
                                .id(2)
                                .pregunta("¿Cuál es la capital de Korea del Sur?")
                                .build(),
                                Question.builder()
                                        .id(3)
                                        .pregunta("¿Cuál es la capital de Japón?")
                                        .build(),
                                Question.builder()
                                        .id(4)
                                        .pregunta("¿Cuál es la capital de Alemania?")
                                        .build()
                        )
                ).build();
    }
}

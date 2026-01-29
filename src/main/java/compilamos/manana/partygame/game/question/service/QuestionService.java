package compilamos.manana.partygame.game.question.service;

import compilamos.manana.partygame.game.model.question.Question;
import compilamos.manana.partygame.game.model.question.QuestionSet;
import compilamos.manana.partygame.game.model.question.RoundQuestions;
import compilamos.manana.partygame.game.question.dtos.QuestionSetNameDTO;
import compilamos.manana.partygame.game.question.repository.QuestionSetRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionSetRepository questionSetReposistory;

    public QuestionService(QuestionSetRepository questionSetRepository){
        this.questionSetReposistory = questionSetRepository;
    }

    // --- 1. CREAR CONJUNTO ---
    public QuestionSet createQuestionSet(QuestionSet questionSet) {
        if (questionSet.getId() != null && questionSetReposistory.existsById(questionSet.getId())) {
            throw new IllegalArgumentException("Ya existe un conjunto con ese ID. Usa editar.");
        }
        return questionSetReposistory.save(questionSet);
    }

    // --- 2. EDITAR CONJUNTO ---
    public QuestionSet updateQuestionSet(String id, QuestionSet datosActualizados) {
        QuestionSet conjuntoExistente = questionSetReposistory.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el conjunto con ID: " + id));

        conjuntoExistente.setConjunto(datosActualizados.getConjunto()); // Nombre tematica
        conjuntoExistente.setIdioma(datosActualizados.getIdioma());
        conjuntoExistente.setCategoria(datosActualizados.getCategoria());

        // Importante: Actualizamos la lista de preguntas si viene en la petición
        if (datosActualizados.getPreguntas() != null) {
            conjuntoExistente.setPreguntas(datosActualizados.getPreguntas());
        }

        return questionSetReposistory.save(conjuntoExistente);
    }

    public List<QuestionSet> getAllQuestionSets() {
        return questionSetReposistory.findAll();
    }

    public Optional<QuestionSet> getQuestionSetById(String id) {
        return questionSetReposistory.findById(id);
    }

    public List<QuestionSetNameDTO> getAllSetNames() {
        return questionSetReposistory.findAllNames();
    }


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

package compilamos.manana.partygame.game.question.service;

import compilamos.manana.partygame.api.exception.ApiException;
import compilamos.manana.partygame.api.exception.ErrorCode;
import compilamos.manana.partygame.game.model.question.Question;
import compilamos.manana.partygame.game.model.question.QuestionSet;
import compilamos.manana.partygame.game.model.question.RoundQuestions;
import compilamos.manana.partygame.game.question.dtos.QuestionSetNameDTO;
import compilamos.manana.partygame.game.question.repository.QuestionSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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


    public RoundQuestions getRandomQuestion(List<String> excludedQuestions, String conjunto) {
    // 1. Manejo de nulos (si es la primera ronda, la lista puede venir null)
        if (excludedQuestions == null) {
            excludedQuestions = Collections.emptyList();
        }
        // 2. BUSCAR EL SET POR NOMBRE (Usando tu metodo getByConjunto)
        Optional<QuestionSet> qs  = Optional.ofNullable(questionSetReposistory.findByConjunto(conjunto)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Conjunto no encontrado", HttpStatus.NOT_FOUND)));

        // 3. Obtener las preguntas de ese set
        List<RoundQuestions> poolDePreguntas = qs.get().getPreguntas();

        // 4. EL FILTRO: Quitamos las que ya están en la lista de jugadas
        final List<String> history = excludedQuestions; // Variable final para el lambda
        List<RoundQuestions> disponibles = poolDePreguntas.stream()
                .filter(r -> !history.contains(r.getId())) // Si el ID ESTÁ en el historial, lo sacamos
                .toList();

        // 5. Verificar si quedan preguntas
        if (disponibles.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR,"Ya no quedan preguntas disponibles.", HttpStatus.NO_CONTENT);
        }

        log.info(disponibles.toString());

        // 6. Elegir una al azar
        Random random = new Random();
        return disponibles.get(random.nextInt(disponibles.size()));
    }
}

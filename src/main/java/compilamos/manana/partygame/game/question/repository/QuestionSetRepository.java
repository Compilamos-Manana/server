package compilamos.manana.partygame.game.question.repository;


import compilamos.manana.partygame.game.model.question.Question;
import compilamos.manana.partygame.game.model.question.QuestionSet;
import compilamos.manana.partygame.game.question.dtos.QuestionSetNameDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionSetRepository extends MongoRepository<QuestionSet, String> {

    // Esta consulta trae TODOS los documentos, pero solo rellena 'id' y 'conjunto'
    @Query(value = "{}", fields = "{ 'conjunto' : 1, '_id' : 1 }")
    List<QuestionSetNameDTO> findAllNames();

    Optional<QuestionSet> findByConjunto(String conjunto);
}

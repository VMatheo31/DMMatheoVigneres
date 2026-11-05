package m2fst.matheo.dm.repository;

import m2fst.matheo.dm.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Integer> {

    // Récupérer toutes les évaluations créées par un évaluateur donné
    List<EvaluationEntity> findByNomEvaluateur(String nomEvaluateur);

    // Récupérer les évaluations contenant un mot clé dans le commentaire
    List<EvaluationEntity> findByCommentaireContainingIgnoreCase(String motCle);
}

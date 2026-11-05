package m2fst.matheo.dm;

import m2fst.matheo.dm.dto.EvaluationDto;
import m2fst.matheo.dm.entity.EvaluationEntity;
import m2fst.matheo.dm.entity.RestaurantEntity;
import m2fst.matheo.dm.exceptions.ForbiddenException;
import m2fst.matheo.dm.exceptions.NotFoundException;
import m2fst.matheo.dm.repository.EvaluationRepository;
import m2fst.matheo.dm.repository.RestaurantRepository;
import m2fst.matheo.dm.repository.S3Repository;
import m2fst.matheo.dm.services.EvaluationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EvaluationServiceTest {

    @InjectMocks
    private EvaluationService evaluationService;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private S3Repository s3Repository;

    @Test
    void should_add_evaluation() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1);
        restaurant.setNom("Le Gourmet");

        EvaluationEntity saved = new EvaluationEntity();
        saved.setId(10);
        saved.setNomEvaluateur("lucien.bramard@michelin.fr");
        saved.setCommentaire("Très bon repas");
        saved.setNote(3);
        saved.setRestaurant(restaurant);

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(evaluationRepository.save(any(EvaluationEntity.class))).thenReturn(saved);

        EvaluationDto result = evaluationService.addEvaluation(1, "lucien.bramard@michelin.fr", "Très bon repas", 3, null);

        assertEquals(10, result.getId());
        assertEquals("lucien.bramard@michelin.fr", result.getNomEvaluateur());
        assertEquals("Très bon repas", result.getCommentaire());
        assertEquals(3, result.getNote());
        verify(evaluationRepository).save(any(EvaluationEntity.class));
    }

    @Test
    void should_throw_not_found_when_adding_evaluation_to_unknown_restaurant() {
        when(restaurantRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                evaluationService.addEvaluation(99, "lucien.bramard@michelin.fr", "Commentaire", 2, null)
        );
    }

    @Test
    void should_delete_own_evaluation_as_user() {
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setId(1);
        evaluation.setNomEvaluateur("lucien.bramard@michelin.fr");

        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));

        evaluationService.deleteEvaluation(1, "lucien.bramard@michelin.fr", false);

        verify(evaluationRepository).deleteById(1);
    }

    @Test
    void should_delete_any_evaluation_as_admin() {
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setId(1);
        evaluation.setNomEvaluateur("lucien.bramard@michelin.fr");

        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));

        evaluationService.deleteEvaluation(1, "noel.flantier@michelin.fr", true);

        verify(evaluationRepository).deleteById(1);
    }

    @Test
    void should_throw_forbidden_when_user_deletes_other_user_evaluation() {
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setId(1);
        evaluation.setNomEvaluateur("autre.user@michelin.fr");

        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));

        assertThrows(ForbiddenException.class, () ->
                evaluationService.deleteEvaluation(1, "lucien.bramard@michelin.fr", false)
        );
    }

    @Test
    void should_get_evaluations_by_keywords() {
        EvaluationEntity eval1 = new EvaluationEntity();
        eval1.setId(1);
        eval1.setCommentaire("Service rapide et plat délicieux");
        eval1.setNote(3);

        EvaluationEntity eval2 = new EvaluationEntity();
        eval2.setId(2);
        eval2.setCommentaire("Trop bruyant");
        eval2.setNote(0);

        when(evaluationRepository.findAll()).thenReturn(List.of(eval1, eval2));

        List<EvaluationDto> result = evaluationService.getEvaluationsByKeywords(List.of("délicieux"));

        assertEquals(1, result.size());
        assertEquals("Service rapide et plat délicieux", result.get(0).getCommentaire());
    }

    @Test
    void should_get_evaluations_by_user() {
        EvaluationEntity eval = new EvaluationEntity();
        eval.setId(1);
        eval.setNomEvaluateur("lucien.bramard@michelin.fr");
        eval.setCommentaire("Super");
        eval.setNote(2);

        when(evaluationRepository.findByNomEvaluateur("lucien.bramard@michelin.fr")).thenReturn(List.of(eval));

        List<EvaluationDto> result = evaluationService.getEvaluationsByUser("lucien.bramard@michelin.fr");

        assertEquals(1, result.size());
        assertEquals("lucien.bramard@michelin.fr", result.get(0).getNomEvaluateur());
    }
}


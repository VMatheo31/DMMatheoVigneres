package m2fst.matheo.dm.services;

import lombok.RequiredArgsConstructor;
import m2fst.matheo.dm.dto.EvaluationDto;
import m2fst.matheo.dm.entity.EvaluationEntity;
import m2fst.matheo.dm.entity.RestaurantEntity;
import m2fst.matheo.dm.exceptions.ForbiddenException;
import m2fst.matheo.dm.exceptions.NotFoundException;
import m2fst.matheo.dm.repository.EvaluationRepository;
import m2fst.matheo.dm.repository.RestaurantRepository;
import m2fst.matheo.dm.repository.S3Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Repository s3Repository;

    // Ajouter une évaluation sur un restaurant
    public EvaluationDto addEvaluation(int restaurantId, String nomEvaluateur, String commentaire, int note,
            List<String> photos) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant non trouvé avec l'id : " + restaurantId));

        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setNomEvaluateur(nomEvaluateur);
        evaluation.setCommentaire(commentaire);
        evaluation.setNote(note);
        evaluation.setRestaurant(restaurant);

        if (photos != null) {
            evaluation.setPhotos(new ArrayList<>(photos));
        }

        EvaluationEntity saved = this.evaluationRepository.save(evaluation);
        return EvaluationDto.fromEntity(saved);
    }

    // Modifier une évaluation (un USER ne peut modifier que les siennes, un ADMIN
    // peut tout modifier)
    public EvaluationDto updateEvaluation(int evaluationId, String commentaire, int note, List<String> photos,
            String nomUtilisateur, boolean isAdmin) {
        EvaluationEntity evaluation = this.evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new NotFoundException("Evaluation non trouvée avec l'id : " + evaluationId));

        if (!isAdmin && !evaluation.getNomEvaluateur().equals(nomUtilisateur)) {
            throw new ForbiddenException("Vous n'avez pas le droit de modifier l'évaluation d'un autre utilisateur");
        }

        evaluation.setCommentaire(commentaire);
        evaluation.setNote(note);
        if (photos != null) {
            evaluation.setPhotos(new ArrayList<>(photos));
        }

        EvaluationEntity saved = this.evaluationRepository.save(evaluation);
        return EvaluationDto.fromEntity(saved);
    }

    // Supprimer une évaluation (un USER ne peut supprimer que les siennes, un ADMIN
    // peut tout supprimer)
    public void deleteEvaluation(int evaluationId, String nomUtilisateur, boolean isAdmin) {
        EvaluationEntity evaluation = this.evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new NotFoundException("Evaluation non trouvée avec l'id : " + evaluationId));

        if (!isAdmin && !evaluation.getNomEvaluateur().equals(nomUtilisateur)) {
            throw new ForbiddenException("Vous n'avez pas le droit de supprimer l'évaluation d'un autre utilisateur");
        }

        this.evaluationRepository.deleteById(evaluationId);
    }

    // Récupérer les évaluations en fonction d'un ou plusieurs mots-clefs
    public List<EvaluationDto> getEvaluationsByKeywords(List<String> keywords) {
        List<EvaluationEntity> allEvaluations = this.evaluationRepository.findAll();
        List<EvaluationDto> result = new ArrayList<>();

        if (keywords == null || keywords.isEmpty()) {
            for (EvaluationEntity eval : allEvaluations) {
                result.add(EvaluationDto.fromEntity(eval));
            }
            return result;
        }

        for (EvaluationEntity eval : allEvaluations) {
            String commentaire = eval.getCommentaire();
            if (commentaire != null) {
                for (String keyword : keywords) {
                    if (commentaire.toLowerCase().contains(keyword.toLowerCase())) {
                        result.add(EvaluationDto.fromEntity(eval));
                        break;
                    }
                }
            }
        }
        return result;
    }

    // Récupérer toutes les évaluations créées par l'utilisateur connecté
    public List<EvaluationDto> getEvaluationsByUser(String nomEvaluateur) {
        List<EvaluationEntity> evaluations = this.evaluationRepository.findByNomEvaluateur(nomEvaluateur);
        return evaluations.stream()
                .map(EvaluationDto::fromEntity)
                .toList();
    }

    // Générer l'URL d'upload S3 pour ajouter une photo à une évaluation
    public String uploadPhoto(int evaluationId, String nomUtilisateur, boolean isAdmin) {
        EvaluationEntity evaluation = this.evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new NotFoundException("Evaluation non trouvée avec l'id : " + evaluationId));

        if (!isAdmin && !evaluation.getNomEvaluateur().equals(nomUtilisateur)) {
            throw new ForbiddenException("Vous n'avez pas le droit de modifier l'évaluation d'un autre utilisateur");
        }

        int photoIndex = evaluation.getPhotos().size() + 1;
        String key = "evaluation-" + evaluationId + "-photo-" + photoIndex;
        String downloadUrl = this.s3Repository.getFileUrl(S3Repository.BUCKET_EVALUATION, key);

        evaluation.getPhotos().add(downloadUrl);
        this.evaluationRepository.save(evaluation);

        return this.s3Repository.getUploadFileUrl(S3Repository.BUCKET_EVALUATION, key);
    }
}
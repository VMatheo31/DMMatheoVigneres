package m2fst.matheo.dm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import m2fst.matheo.dm.entity.EvaluationEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationDto {

    private int id;
    private String nomEvaluateur;
    private String commentaire;
    private int note;
    private List<String> photos;
    private int restaurantId;

    public EvaluationDto(EvaluationEntity entity) {
        this.id = entity.getId();
        this.nomEvaluateur = entity.getNomEvaluateur();
        this.commentaire = entity.getCommentaire();
        this.note = entity.getNote();

        if (entity.getPhotos() != null) {
            this.photos = new ArrayList<>(entity.getPhotos());
        } else {
            this.photos = new ArrayList<>();
        }

        if (entity.getRestaurant() != null) {
            this.restaurantId = entity.getRestaurant().getId();
        }
    }

    public static EvaluationDto fromEntity(EvaluationEntity entity) {
        return new EvaluationDto(entity);
    }
}

package m2fst.matheo.dm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import m2fst.matheo.dm.entity.RestaurantEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {

    private int id;
    private String nom;
    private String adresse;
    private String image;
    private List<EvaluationDto> evaluations;
    private double moyenne;

    public static RestaurantDto fromEntity(RestaurantEntity entity, double moyenne) {
        List<EvaluationDto> evalDtos = new ArrayList<>();
        if (entity.getEvaluations() != null) {
            evalDtos = entity.getEvaluations().stream()
                    .map(EvaluationDto::fromEntity)
                    .collect(Collectors.toList());
        }

        return RestaurantDto.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .adresse(entity.getAdresse())
                .image(entity.getImage())
                .evaluations(evalDtos)
                .moyenne(moyenne)
                .build();
    }
}

package m2fst.matheo.dm.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationCreateDto {

    @NotBlank(message = "Le commentaire est obligatoire")
    @Size(max = 255, message = "Le commentaire ne doit pas dépasser 255 caractères")
    private String commentaire;

    @Min(value = 0, message = "La note doit être comprise entre 0 et 3")
    @Max(value = 3, message = "La note doit être comprise entre 0 et 3")
    private int note;

    private List<String> photos;

    // Je récupere le nomEvaluateur sur le JWT
}

package m2fst.matheo.dm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantCreateDto {

    @NotBlank(message = "Le nom du restaurant est obligatoire")
    @Size(max = 90, message = "Le nom ne doit pas dépasser 90 caractères")
    private String nom;

    @NotBlank(message = "L'adresse du restaurant est obligatoire")
    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;
}


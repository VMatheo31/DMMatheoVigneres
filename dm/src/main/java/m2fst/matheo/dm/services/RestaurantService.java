package m2fst.matheo.dm.services;

import lombok.RequiredArgsConstructor;
import m2fst.matheo.dm.dto.RestaurantDto;
import m2fst.matheo.dm.entity.EvaluationEntity;
import m2fst.matheo.dm.entity.RestaurantEntity;
import m2fst.matheo.dm.exceptions.NotFoundException;
import m2fst.matheo.dm.repository.RestaurantRepository;
import m2fst.matheo.dm.repository.S3Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final S3Repository s3Repository;

    // Calcule la moyenne des notes d'un restaurant (-1 si aucune évaluation)
    public double calculerMoyenne(RestaurantEntity restaurant) {
        List<EvaluationEntity> evaluations = restaurant.getEvaluations();
        if (evaluations == null || evaluations.isEmpty()) {
            return -1;
        }

        double somme = 0;
        for (EvaluationEntity eval : evaluations) {
            somme = somme + eval.getNote();
        }
        return somme / evaluations.size();
    }

    // Récupere tous les restaurants avec leur moyenne
    public List<RestaurantDto> getAllRestaurants() {
        List<RestaurantEntity> restaurants = this.restaurantRepository.findAll();
        List<RestaurantDto> result = new ArrayList<>();

        for (RestaurantEntity restaurant : restaurants) {
            double moyenne = calculerMoyenne(restaurant);
            result.add(RestaurantDto.fromEntity(restaurant, moyenne));
        }
        return result;
    }

    // Récupérer un restaurant en particulier avec sa moyenne
    public RestaurantDto getRestaurantById(int id) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant non trouvé avec l'id : " + id));

        double moyenne = calculerMoyenne(restaurant);
        return RestaurantDto.fromEntity(restaurant, moyenne);
    }

    // Créer un nouveau restaurant (retourne aussi la moyenne qui vaut -1 au début)
    public RestaurantDto createRestaurant(String nom, String adresse) {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setNom(nom);
        restaurant.setAdresse(adresse);

        RestaurantEntity saved = this.restaurantRepository.save(restaurant);
        double moyenne = calculerMoyenne(saved);
        return RestaurantDto.fromEntity(saved, moyenne);
    }

    // Mettre à jour le nom et l'adresse d'un restaurant
    public RestaurantDto updateRestaurant(int id, String nom, String adresse) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant non trouvé avec l'id : " + id));

        restaurant.setNom(nom);
        restaurant.setAdresse(adresse);

        RestaurantEntity saved = this.restaurantRepository.save(restaurant);
        double moyenne = calculerMoyenne(saved);
        return RestaurantDto.fromEntity(saved, moyenne);
    }

    // Générer l'URL d'upload S3 pour l'image d'un restaurant
    public String uploadImage(int id) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant non trouvé avec l'id : " + id));

        String key = "restaurant-" + id;
        String downloadUrl = this.s3Repository.getFileUrl(S3Repository.BUCKET_RESTAURANT, key);
        restaurant.setImage(downloadUrl);
        this.restaurantRepository.save(restaurant);

        return this.s3Repository.getUploadFileUrl(S3Repository.BUCKET_RESTAURANT, key);
    }
}

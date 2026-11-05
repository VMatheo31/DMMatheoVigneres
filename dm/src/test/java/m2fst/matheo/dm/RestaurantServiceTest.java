package m2fst.matheo.dm;

import m2fst.matheo.dm.dto.RestaurantDto;
import m2fst.matheo.dm.entity.EvaluationEntity;
import m2fst.matheo.dm.entity.RestaurantEntity;
import m2fst.matheo.dm.exceptions.NotFoundException;
import m2fst.matheo.dm.repository.RestaurantRepository;
import m2fst.matheo.dm.repository.S3Repository;
import m2fst.matheo.dm.services.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private S3Repository s3Repository;

    @Test
    void should_return_minus_one_when_restaurant_has_no_evaluation() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1);
        restaurant.setNom("Le Gourmet");
        restaurant.setAdresse("12 rue de Paris");
        restaurant.setEvaluations(new ArrayList<>());

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        RestaurantDto result = restaurantService.getRestaurantById(1);

        assertEquals(-1.0, result.getMoyenne());
        assertEquals("Le Gourmet", result.getNom());
    }

    @Test
    void should_calculate_average_when_restaurant_has_evaluations() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1);
        restaurant.setNom("Le Gourmet");
        restaurant.setAdresse("12 rue de Paris");

        EvaluationEntity eval1 = new EvaluationEntity();
        eval1.setId(1);
        eval1.setNote(3);
        eval1.setRestaurant(restaurant);

        EvaluationEntity eval2 = new EvaluationEntity();
        eval2.setId(2);
        eval2.setNote(1);
        eval2.setRestaurant(restaurant);

        List<EvaluationEntity> evaluations = new ArrayList<>();
        evaluations.add(eval1);
        evaluations.add(eval2);
        restaurant.setEvaluations(evaluations);

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        RestaurantDto result = restaurantService.getRestaurantById(1);

        assertEquals(2.0, result.getMoyenne());
    }

    @Test
    void should_get_all_restaurants() {
        RestaurantEntity r1 = new RestaurantEntity();
        r1.setId(1);
        r1.setNom("Resto 1");
        r1.setEvaluations(new ArrayList<>());

        RestaurantEntity r2 = new RestaurantEntity();
        r2.setId(2);
        r2.setNom("Resto 2");
        EvaluationEntity eval = new EvaluationEntity();
        eval.setNote(3);
        r2.setEvaluations(List.of(eval));

        when(restaurantRepository.findAll()).thenReturn(List.of(r1, r2));

        List<RestaurantDto> result = restaurantService.getAllRestaurants();

        assertEquals(2, result.size());
        assertEquals(-1.0, result.get(0).getMoyenne());
        assertEquals(3.0, result.get(1).getMoyenne());
    }

    @Test
    void should_throw_not_found_when_getting_unknown_restaurant() {
        when(restaurantRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> restaurantService.getRestaurantById(99));
    }

    @Test
    void should_create_restaurant() {
        RestaurantEntity saved = new RestaurantEntity();
        saved.setId(1);
        saved.setNom("Chez Paul");
        saved.setAdresse("5 avenue Foch");
        saved.setEvaluations(new ArrayList<>());

        when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(saved);

        RestaurantDto result = restaurantService.createRestaurant("Chez Paul", "5 avenue Foch");

        assertEquals("Chez Paul", result.getNom());
        assertEquals("5 avenue Foch", result.getAdresse());
        assertEquals(-1.0, result.getMoyenne());
        verify(restaurantRepository).save(any(RestaurantEntity.class));
    }

    @Test
    void should_update_restaurant() {
        int id = 1;
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(id);
        restaurant.setNom("Ancien Nom");
        restaurant.setAdresse("Ancienne Adresse");
        restaurant.setEvaluations(new ArrayList<>());

        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        RestaurantDto result = restaurantService.updateRestaurant(id, "Nouveau Nom", "Nouvelle Adresse");

        assertEquals("Nouveau Nom", result.getNom());
        assertEquals("Nouvelle Adresse", result.getAdresse());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void should_throw_not_found_when_updating_unknown_restaurant() {
        when(restaurantRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> restaurantService.updateRestaurant(99, "Nom", "Adresse"));
    }
}


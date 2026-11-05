package m2fst.matheo.dm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m2fst.matheo.dm.dto.ImagePutDto;
import m2fst.matheo.dm.dto.RestaurantCreateDto;
import m2fst.matheo.dm.dto.RestaurantDto;
import m2fst.matheo.dm.services.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/restaurants")
    public List<RestaurantDto> getAllRestaurants() {
        return this.restaurantService.getAllRestaurants();
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable(name = "id") int id) {
        return ResponseEntity.ok(this.restaurantService.getRestaurantById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantCreateDto restaurant) {
        return ResponseEntity.ok(this.restaurantService.createRestaurant(restaurant.getNom(), restaurant.getAdresse()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable(name = "id") int id, @Valid @RequestBody RestaurantCreateDto restaurant) {
        return ResponseEntity.ok(this.restaurantService.updateRestaurant(id, restaurant.getNom(), restaurant.getAdresse()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}/image")
    public ResponseEntity<ImagePutDto> uploadImage(@PathVariable(name = "id") int id) {
        return ResponseEntity.ok(ImagePutDto.fromEntity(this.restaurantService.uploadImage(id)));
    }
}


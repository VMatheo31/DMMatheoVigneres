package m2fst.matheo.dm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Restaurant")
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nom", length = 90, nullable = false)
    private String nom;

    @Column(name = "adresse", length = 255, nullable = false)
    private String adresse;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationEntity> evaluations = new ArrayList<>();
}

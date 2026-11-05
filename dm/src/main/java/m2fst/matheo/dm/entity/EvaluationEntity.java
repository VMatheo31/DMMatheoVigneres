package m2fst.matheo.dm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Evaluation")
public class EvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nomEvaluateur", length = 50, nullable = false)
    private String nomEvaluateur;

    @Column(name = "commentaire", length = 255, nullable = false)
    private String commentaire;

    @Column(name = "note", nullable = false)
    private int note;

    @ElementCollection
    @CollectionTable(name = "Photos", joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "photo")
    private List<String> photos = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;
}

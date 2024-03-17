package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "local_places_lift_gates")
@Data
public class LocalPlaceLiftGate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "lon")
    private Double lon;
    @Column(name = "lat")
    private Double lat;
}


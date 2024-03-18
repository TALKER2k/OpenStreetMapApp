package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "local_places_lift_gates")
@Data
public class LocalPlaceLiftGate {
    @Id
    @Column(name = "gates_id", unique = true)
    private Long gatesId;
    @Column(name = "lon")
    private Double lon;
    @Column(name = "lat")
    private Double lat;
    @Column(name = "name")
    private String name;
}


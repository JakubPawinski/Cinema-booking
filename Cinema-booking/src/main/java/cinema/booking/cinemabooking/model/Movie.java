package cinema.booking.cinemabooking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;

    private String genre;
    private int durationMin; // duration in minutes

    private String imageUrl;
    private String trailerUrl;

    private String director;
    private String mainCast;
    private String ageRating;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Seance> seances = new ArrayList<>();
}

package cinema.booking.cinemabooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a movie.
 */
@Entity
@Getter
@Setter
@ToString
public class Movie {

    /**
     * Unique identifier for the movie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the movie.
     */
    @NotBlank(message = "Movie title is required")
    @Column(nullable = false)
    private String title;

    /**
     * Description of the movie.
     */
    @Column(length = 2000)
    private String description;

    /**
     * Genre of the movie.
     */
    private String genre;

    /**
     * Duration of the movie in minutes.
     */
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMin;

    /**
     * URL of the movie's poster image.
     */
    private String imageUrl;

    /**
     * URL of the movie's trailer.
     */
    @URL(message = "Invalid trailer URL")
    private String trailerUrl;

    /**
     * Director of the movie.
     */
    private String director;

    /**
     * Main cast of the movie.
     */
    private String mainCast;

    /**
     * Age rating of the movie.
     */
    private String ageRating;

    /**
     * List of seances for the movie.
     */
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Exclude seances from toString to prevent circular dependencies
    private List<Seance> seances = new ArrayList<>();

    /**
     * Gallery images for the movie.
     */
    @ElementCollection
    @CollectionTable(name = "movie_gallery", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "image_url")
    private List<String> galleryImages = new ArrayList<>();
}

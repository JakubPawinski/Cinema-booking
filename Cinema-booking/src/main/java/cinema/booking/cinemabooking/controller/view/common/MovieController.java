package cinema.booking.cinemabooking.controller.view.common;

import org.springframework.ui.Model;
import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View Controller for Movie Listings and Details
 */
@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    /**
     * Display list of movies
     * @return movie list view
     */
    @GetMapping
    public String moviesView() {
        return "movie";
    }

    /**
     * Display movie detail page
     * @param movieId ID of the movie
     * @param model Spring MVC model
     * @return movie detail view
     */
    @GetMapping("/{movieId}")
    public String movieDetailView(@PathVariable Long movieId, Model model) {
        MovieDto movie = movieService.getMovieById(movieId);
        model.addAttribute("movie", movie);
        return "movie-detail";
    }
}

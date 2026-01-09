package cinema.booking.cinemabooking.controller.view;

import org.springframework.ui.Model;
import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieViewController {
    private final MovieService movieService;

    @GetMapping
    public String moviesView() {
        return "movie";
    }

    @GetMapping("/{movieId}")
    public String movieDetailView(@PathVariable Long movieId, Model model) {
        MovieDto movie = movieService.getMovieById(movieId);
        model.addAttribute("movie", movie);
        return "movie-detail";
    }
}

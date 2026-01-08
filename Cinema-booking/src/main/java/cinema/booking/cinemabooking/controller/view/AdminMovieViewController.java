package cinema.booking.cinemabooking.controller.view;

import cinema.booking.cinemabooking.dto.MovieDto;
import cinema.booking.cinemabooking.dto.MovieRequestDto;
import cinema.booking.cinemabooking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieViewController {
    private final MovieService movieService;

    @GetMapping
    public String listMovies(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<MovieDto> moviePage = movieService.getAllMovies(pageable);

        model.addAttribute("movies", moviePage);
        return "admin/movies-list";
    }

    @GetMapping("/add")
    public String addMovieForm(Model model) {
        model.addAttribute("movie", new MovieRequestDto());
        model.addAttribute("isEdit", false);
        return "admin/movie-form";
    }

    @PostMapping("/add")
    public String addMovie(@ModelAttribute("movie") MovieRequestDto dto) {
        movieService.addMovie(dto);
        return "redirect:/admin/movies"; // Przekierowanie po sukcesie
    }

    // 4. FORMULARZ EDYCJI (Pobieramy dane filmu)
    @GetMapping("/edit/{id}")
    public String editMovieForm(@PathVariable Long id, Model model) {
        MovieDto movieDto = movieService.getMovieById(id);

        // Mapujemy Dto na RequestDto (żeby formularz pasował)
        // W prawdziwym projekcie użyłbyś do tego mappera, tu zrobimy ręcznie dla uproszczenia
        MovieRequestDto requestDto = new MovieRequestDto();
        requestDto.setTitle(movieDto.getTitle());
        requestDto.setDescription(movieDto.getDescription());
        requestDto.setGenre(movieDto.getGenre());
        requestDto.setDurationMin(movieDto.getDurationMin());
        requestDto.setImageUrl(movieDto.getImageUrl());
        requestDto.setTrailerUrl(movieDto.getTrailerUrl());
        requestDto.setDirector(movieDto.getDirector());
        requestDto.setMainCast(movieDto.getMainCast());
        requestDto.setAgeRating(movieDto.getAgeRating());

        model.addAttribute("movie", requestDto);
        model.addAttribute("movieId", id); // Potrzebne do action URL
        model.addAttribute("isEdit", true);

        return "admin/movie-form";
    }

    // 5. ZAPISYWANIE ZMIAN (POST - Update)
    @PostMapping("/edit/{id}")
    public String updateMovie(@PathVariable Long id, @ModelAttribute("movie") MovieRequestDto dto) {
        movieService.updateMovie(id, dto);
        return "redirect:/admin/movies";
    }

    // 6. USUWANIE (POST)
    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return "redirect:/admin/movies";
    }
}

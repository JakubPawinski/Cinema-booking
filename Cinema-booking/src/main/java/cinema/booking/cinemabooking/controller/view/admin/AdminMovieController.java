package cinema.booking.cinemabooking.controller.view.admin;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.mapper.MovieMapper;
import cinema.booking.cinemabooking.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * View Controller for Admin Movie Management
 */
@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
@Slf4j
public class AdminMovieController {
    private final MovieService movieService;
    private final MovieMapper movieMapper;

    /**
     * List movies with pagination
     * @param model Spring MVC model
     * @param page Page number (default 0)
     * @param size Page size (default 10)
     * @return movies list view
     */
    @GetMapping
    public String listMovies(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {

        // Create pageable with sorting by ID descending
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<MovieDto> moviePage = movieService.getAllMovies(pageable);

        model.addAttribute("movies", moviePage);
        return "admin/movies-list";
    }

    /**
     * Display form to add a new movie
     * @param model Spring MVC model
     * @return movie form view
     */
    @GetMapping("/add")
    public String addMovieForm(Model model) {
        model.addAttribute("movie", new MovieRequestDto());
        model.addAttribute("isEdit", false); // Flag to toggle UI elements
        return "admin/movie-form";
    }

    /**
     * Handle submission of new movie
     * @param dto MovieRequestDto with form data
     * @return redirect to movies list
     */
    @PostMapping("/add")
    public String addMovie(@ModelAttribute("movie") MovieRequestDto dto) {
        movieService.addMovie(dto);
        return "redirect:/admin/movies";
    }

    /**
     * Display form to edit an existing movie
     * @param id Movie ID
     * @param model Spring MVC model
     * @return movie form view
     */
    @GetMapping("/edit/{id}")
    public String editMovieForm(@PathVariable Long id, Model model) {
        // Fetch existing movie data
        MovieDto movieDto = movieService.getMovieById(id);

        // Map to request DTO
        MovieRequestDto requestDto = movieMapper.toRequestDto(movieDto);

        model.addAttribute("movie", requestDto);
        model.addAttribute("movieId", id);
        model.addAttribute("isEdit", true);

        model.addAttribute("currentGallery", movieDto.getGalleryImages());

        return "admin/movie-form";
    }

    /**
     * Handle submission of edited movie
     * @param id Movie ID
     * @param dto MovieRequestDto with form data
     * @return redirect to movies list
     */
    @PostMapping("/edit/{id}")
    public String updateMovie(@PathVariable Long id, @ModelAttribute("movie") MovieRequestDto dto) {
        log.info("Admin: Updating movie ID: {}", id);
        movieService.updateMovie(id, dto);
        return "redirect:/admin/movies";
    }

    /**
     * Handle deletion of a movie
     * @param id Movie ID
     * @return redirect to movies list
     */
    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        log.info("Admin: Deleting movie ID: {}", id);
        movieService.deleteMovie(id);
        return "redirect:/admin/movies";
    }

    /**
     * Handle deletion of a gallery image from a movie
     * @param id Movie ID
     * @param imagePath Path of the image to delete
     * @return redirect to edit movie form
     */
    @DeleteMapping("/edit/{id}/gallery")
    public String deleteGalleryImage(@PathVariable Long id, @RequestParam("image") String imagePath) {
        movieService.removeGalleryImage(id, imagePath);
        return "redirect:/admin/movies/edit/" + id;
    }
}

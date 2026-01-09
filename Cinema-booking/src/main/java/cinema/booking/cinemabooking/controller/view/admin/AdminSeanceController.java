package cinema.booking.cinemabooking.controller.view.admin;

import cinema.booking.cinemabooking.dto.request.SeanceRequestDto;
import cinema.booking.cinemabooking.repository.CinemaRoomRepository;
import cinema.booking.cinemabooking.repository.MovieRepository;
import cinema.booking.cinemabooking.service.SeanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * View Controller for Admin Seance Management
 */
@Controller
@RequestMapping("/admin/seances")
@RequiredArgsConstructor
@Slf4j
public class AdminSeanceController {

    private final SeanceService seanceService;
    private final MovieRepository movieRepository;
    private final CinemaRoomRepository roomRepository;

    /**
     * Display list of seances
     * @param model Spring MVC model
     * @return The seances list view
     */
    @GetMapping
    public String listSeances(Model model) {
        //TODO: Add pagination
        model.addAttribute("seances", seanceService.getAllSeances());
        return "admin/seances-list";
    }

    /**
     * Display form to add a new seance
     * @param model Spring MVC model
     * @return The seance form view
     */
    @GetMapping("/add")
    public String addSeanceForm(Model model) {
        model.addAttribute("seance", new SeanceRequestDto());
        model.addAttribute("movies", movieRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());

        return "admin/seance-form";
    }

    /**
     * Handle submission of new seance form
     * @param dto Seance request data
     * @param redirectAttributes Attributes for redirect messages
     * @param model Spring MVC model
     * @return Redirect to seances list on success, or back to form on validation error
     */
    @PostMapping("/add")
    public String createSeance(@ModelAttribute("seance") SeanceRequestDto dto,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            log.info("View: Creating new seance with data: {}", dto);

            seanceService.createSeance(dto);
            redirectAttributes.addFlashAttribute("success", "Dodano nowy seans!");

            return "redirect:/admin/seances";
        } catch (IllegalStateException e) {
            log.warn("Admin: Failed to create seance: {}", e.getMessage());

            // Add error message to display in the form
            model.addAttribute("error", e.getMessage());

            model.addAttribute("movies", movieRepository.findAll());
            model.addAttribute("rooms", roomRepository.findAll());
            return "admin/seance-form";
        }
    }

    /**
     * Handle deletion of a seance
     * @param id Seance ID
     * @return Redirect to seances list
     */
    @PostMapping("/delete/{id}")
    public String deleteSeance(@PathVariable Long id) {
        log.info("Admin: Deleting seance ID: {}", id);

        seanceService.deleteSeance(id);
        return "redirect:/admin/seances";
    }
}

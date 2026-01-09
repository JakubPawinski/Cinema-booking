package cinema.booking.cinemabooking.controller.view;

import cinema.booking.cinemabooking.dto.request.SeanceRequestDto;
import cinema.booking.cinemabooking.repository.CinemaRoomRepository;
import cinema.booking.cinemabooking.repository.MovieRepository;
import cinema.booking.cinemabooking.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/seances")
@RequiredArgsConstructor
public class AdminSeanceViewController {

    private final SeanceService seanceService;
    private final MovieRepository movieRepository;      // Potrzebne do listy rozwijanej
    private final CinemaRoomRepository roomRepository;  // Potrzebne do listy rozwijanej

    // 1. LISTA SEANSÓW
    @GetMapping
    public String listSeances(Model model) {
        // Dla uproszczenia pobieramy wszystkie. W produkcji dodałbyś stronicowanie (Pageable)
        model.addAttribute("seances", seanceService.getAllSeances());
        return "admin/seances-list";
    }

    // 2. FORMULARZ DODAWANIA
    @GetMapping("/add")
    public String addSeanceForm(Model model) {
        model.addAttribute("seance", new SeanceRequestDto());

        // Przekazujemy listy do Selectów
        model.addAttribute("movies", movieRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());

        return "admin/seance-form";
    }

    // 3. ZAPISYWANIE SEANSU (z obsługą błędu walidacji)
    @PostMapping("/add")
    public String createSeance(@ModelAttribute("seance") SeanceRequestDto dto,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            seanceService.createSeance(dto);
            redirectAttributes.addFlashAttribute("success", "Dodano nowy seans!");
            return "redirect:/admin/seances";
        } catch (IllegalStateException e) {
            // Błąd walidacji (nakładanie się terminów)
            model.addAttribute("error", e.getMessage());

            // Musimy ponownie załadować listy do selectów, bo wracamy do widoku formularza
            model.addAttribute("movies", movieRepository.findAll());
            model.addAttribute("rooms", roomRepository.findAll());
            return "admin/seance-form";
        }
    }

    // 4. USUWANIE
    @PostMapping("/delete/{id}")
    public String deleteSeance(@PathVariable Long id) {
        seanceService.deleteSeance(id);
        return "redirect:/admin/seances";
    }
}

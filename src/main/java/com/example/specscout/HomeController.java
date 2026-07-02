package com.example.specscout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final PhoneRepository phoneRepository;
    private static final int PAGE_SIZE = 6;

    public HomeController(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String brand,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        List<Phone> all;
        if (q != null && !q.isBlank()) {
            all = phoneRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(q, q);
        } else {
            all = phoneRepository.findAll();
        }

        // filter by brand if one is selected
        if (brand != null && !brand.isBlank() && !brand.equalsIgnoreCase("all")) {
            all = all.stream()
                     .filter(p -> brand.equalsIgnoreCase(p.getBrand()))
                     .collect(Collectors.toList());
        }

        all.sort((a, b) -> safe(a.getName()).compareToIgnoreCase(safe(b.getName())));

        // build the list of all brands (for the buttons)
        List<String> brands = phoneRepository.findAll().stream()
                .map(Phone::getBrand)
                .filter(bn -> bn != null && !bn.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        int total = all.size();
        int totalPages = (int) Math.ceil(total / (double) PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (page < 0) page = 0;
        if (page > totalPages - 1) page = totalPages - 1;

        int from = page * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, total);
        List<Phone> pagePhones = new ArrayList<>(all.subList(from, to));

        model.addAttribute("phones", pagePhones);
        model.addAttribute("q", q);
        model.addAttribute("brand", brand);
        model.addAttribute("brands", brands);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("total", total);
        model.addAttribute("favCount", phoneRepository.findByFavoriteTrue().size());
        return "home";
    }

    @GetMapping("/phone/{id}")
    public String phoneDetail(@PathVariable Long id, Model model) {
        Phone phone = phoneRepository.findById(id).orElse(null);
        if (phone == null) {
            return "redirect:/";
        }
        model.addAttribute("phone", phone);
        return "phone";
    }

    @PostMapping("/phone/{id}/favorite")
    public String toggleFavorite(@PathVariable Long id,
                                 @RequestParam(defaultValue = "/") String returnTo) {
        phoneRepository.findById(id).ifPresent(phone -> {
            phone.setFavorite(!phone.isFavorite());
            phoneRepository.save(phone);
        });
        return "redirect:" + returnTo;
    }

    @GetMapping("/favorites")
    public String favorites(Model model) {
        List<Phone> favs = phoneRepository.findByFavoriteTrue();
        model.addAttribute("phones", favs);
        model.addAttribute("favCount", favs.size());
        return "favorites";
    }

    private String safe(String s) { return s == null ? "" : s; }
}
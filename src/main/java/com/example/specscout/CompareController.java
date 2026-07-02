package com.example.specscout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CompareController {

    private final PhoneRepository phoneRepository;

    public CompareController(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @GetMapping("/compare")
    public String compare(@RequestParam(required = false) List<Long> ids, Model model) {
        List<Phone> phones = new ArrayList<>();
        if (ids != null) {
            for (Long id : ids) {
                phoneRepository.findById(id).ifPresent(phones::add);
            }
        }

      

        List<Long> bestBatteryIds = idsWithMax(phones, "battery");
        List<Long> bestRamIds = idsWithMax(phones, "ram");

        Long clearWinnerId = null;
        for (Phone p : phones) {
            boolean leadsRam = bestRamIds.contains(p.getId());
            boolean leadsBattery = bestBatteryIds.contains(p.getId());
            boolean winsRamAlone = leadsRam && bestRamIds.size() == 1;
            boolean winsBatteryAlone = leadsBattery && bestBatteryIds.size() == 1;

            if (leadsRam && leadsBattery && (winsRamAlone || winsBatteryAlone)) {
                clearWinnerId = p.getId();
                break;
            }
        }

        String performancePick = null;
        String batteryPick = null;
        if (clearWinnerId == null) {
            performancePick = namesFor(phones, bestRamIds);
            batteryPick = namesFor(phones, bestBatteryIds);
        }

        model.addAttribute("phones", phones);
        model.addAttribute("bestBatteryIds", bestBatteryIds);
        model.addAttribute("bestRamIds", bestRamIds);
        model.addAttribute("clearWinnerId", clearWinnerId);
        model.addAttribute("performancePick", performancePick);
        model.addAttribute("batteryPick", batteryPick);
        return "compare";
    }

    private String namesFor(List<Phone> phones, List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        List<String> names = phones.stream()
                .filter(p -> ids.contains(p.getId()))
                .map(Phone::getName)
                .collect(Collectors.toList());
        if (names.isEmpty()) return null;
        if (names.size() == 1) return names.get(0);
        if (names.size() == 2) return names.get(0) + " or " + names.get(1);
        String last = names.remove(names.size() - 1);
        return String.join(", ", names) + " or " + last;
    }

    private List<Long> idsWithMax(List<Phone> phones, String field) {
        int best = Integer.MIN_VALUE;
        for (Phone p : phones) {
            int num = field.equals("battery") ? extractBattery(p.getBattery()) : extractRam(p.getRam());
            best = Math.max(best, num);
        }
        List<Long> ids = new ArrayList<>();
        if (best <= 0) return ids;
        for (Phone p : phones) {
            int num = field.equals("battery") ? extractBattery(p.getBattery()) : extractRam(p.getRam());
            if (num == best) ids.add(p.getId());
        }
        return ids;
    }

    // Normalize invisible/odd whitespace so the pattern always matches
    private String normalize(String s) {
        if (s == null) return null;
        // replace ALL kinds of unicode spaces (incl. non-breaking) with a normal space
        return s.replaceAll("[\\u00A0\\u2007\\u202F\\s]+", " ").trim();
    }

    // Real phone RAM is 1–24 GB. Larger numbers are mislabeled storage → ignored.
    private int extractRam(String s) {
        s = normalize(s);
        if (s == null) return -1;
        int best = -1;
        Matcher m = Pattern.compile("(\\d+)\\s*GB|GB\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(s);
        while (m.find()) {
            for (int g = 1; g <= 2; g++) {
                String grp = m.group(g);
                if (grp != null) {
                    try {
                        int val = Integer.parseInt(grp);
                        if (val >= 1 && val <= 24) best = Math.max(best, val);
                    } catch (Exception ignored) {}
                }
            }
        }
        return best;
    }

    private int extractBattery(String s) {
        s = normalize(s);
        if (s == null) return -1;
        Matcher m = Pattern.compile("(\\d+)\\s*mAh", Pattern.CASE_INSENSITIVE).matcher(s);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception ignored) {}
        }
        String digits = s.replaceAll("[^0-9]", "");
        try { return digits.isEmpty() ? -1 : Integer.parseInt(digits); } catch (Exception e) { return -1; }
    }
}
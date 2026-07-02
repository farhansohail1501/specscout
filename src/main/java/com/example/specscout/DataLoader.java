package com.example.specscout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final PhoneRepository phoneRepository;

    public DataLoader(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (phoneRepository.count() > 0) {
            System.out.println("✅ Phones already loaded (" + phoneRepository.count() + "). Skipping seed.");
            return;
        }

        ClassPathResource resource = new ClassPathResource("phones.tsv");
        if (!resource.exists()) {
            System.out.println("⚠️ No phones.tsv found. Database is empty.");
            return;
        }

        List<Phone> phones = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\t", -1);
                if (parts.length < 5) continue;
                Phone p = new Phone();
                p.setName(parts[0]);
                p.setBrand(parts[1]);
                p.setRam(parts[2]);
                p.setBattery(parts[3]);
                p.setImage(parts[4]);
                p.setFavorite(false);
                phones.add(p);
            }
        }

        phoneRepository.saveAll(phones);
        System.out.println("🌱 Seeded " + phones.size() + " phones from phones.tsv");
    }
}
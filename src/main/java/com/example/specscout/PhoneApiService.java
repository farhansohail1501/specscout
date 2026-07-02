package com.example.specscout;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PhoneApiService {

    @Value("${mobileapi.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    public List<Phone> search(String name) {
        List<Phone> phones = new ArrayList<>();
        try {
            DeviceSearchResponse response = restClient.get()
                .uri("https://api.mobileapi.dev/devices/search?name={name}&key={key}", name, apiKey)
                .retrieve()
                .body(DeviceSearchResponse.class);

            if (response != null && response.devices != null) {
                for (Device d : response.devices) {
                    if (d.device_type == null || !d.device_type.equalsIgnoreCase("phone")) {
                        continue;
                    }

                    String n = clean(d.name);
                    String brand = clean(d.manufacturer_name);
                    String ram = clean(d.hardware);
                    String battery = clean(d.battery_capacity);
                    String image = d.image_b64;

                    if (isBlank(n) || isBlank(brand) || isBlank(ram) || isBlank(battery) || isBlank(image)) {
                        continue;
                    }

                    Phone p = new Phone();
                    p.setName(n);
                    p.setBrand(brand);
                    p.setRam(ram);
                    p.setBattery(battery);
                    p.setImage(image);
                    p.setStorage(clean(d.storage));
                    p.setScreen(clean(d.screen_resolution));
                    p.setCamera(clean(d.camera));
                    p.setReleaseDate(clean(d.release_date));
                    phones.add(p);
                }
            }
        } catch (Exception e) {
            System.out.println("API error: " + e.getMessage());
        }
        return phones;
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private String clean(String s) {
        if (s == null) return null;
        return s.trim().replaceAll(",\\s*$", "");
    }
}
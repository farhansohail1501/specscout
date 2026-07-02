package com.example.specscout;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {

    List<Phone> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String brand);

    List<Phone> findByFavoriteTrue();
}
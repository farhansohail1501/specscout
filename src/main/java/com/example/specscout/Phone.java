package com.example.specscout;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String brand;
    private String price;
    private String ram;
    private String battery;
    private boolean favorite = false;
    private String storage;
    private String screen;
    private String camera;
    private String releaseDate;

    @Column(columnDefinition = "TEXT")
    private String image;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getRam() { return ram; }
    public void setRam(String ram) { this.ram = ram; }

    public String getBattery() { return battery; }
    public void setBattery(String battery) { this.battery = battery; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }

    public String getScreen() { return screen; }
    public void setScreen(String screen) { this.screen = screen; }

    public String getCamera() { return camera; }
    public void setCamera(String camera) { this.camera = camera; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    
}
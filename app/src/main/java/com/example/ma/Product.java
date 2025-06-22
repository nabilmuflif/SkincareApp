package com.example.ma;

public class Product {
    private int id;
    private String name;
    private String brand;
    private String ingredients;
    private String category;
    private long createdAt;
    private boolean isFavorite; // BARU

    public Product() {}

    public Product(String name, String brand, String ingredients, String category) {
        this.name = name;
        this.brand = brand;
        this.ingredients = ingredients;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
        this.isFavorite = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // GETTER & SETTER BARU
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
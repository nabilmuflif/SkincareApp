package com.example.ma;

public class Ingredient {
    private int id;
    private String name;
    private String commonName;
    private String rating;
    private String whatItDoes;
    private String description;
    private String irritancyLevel;
    private String sources;
    private boolean isFavorite;

    public Ingredient() {
        // Diperlukan konstruktor kosong
    }

    public Ingredient(String name, String commonName, String rating, String whatItDoes,
                      String description, String irritancyLevel) {
        this.name = name;
        this.commonName = commonName;
        this.rating = rating;
        this.whatItDoes = whatItDoes;
        this.description = description;
        this.irritancyLevel = irritancyLevel;
        this.isFavorite = false;
    }

    // --- GETTERS DAN SETTERS (INI YANG MEMPERBAIKI ERROR) ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getWhatItDoes() {
        return whatItDoes;
    }

    public void setWhatItDoes(String whatItDoes) {
        this.whatItDoes = whatItDoes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIrritancyLevel() {
        return irritancyLevel;
    }

    public void setIrritancyLevel(String irritancyLevel) {
        this.irritancyLevel = irritancyLevel;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // --- METODE HELPER ---

    public int getRatingColor() {
        if (rating == null) {
            return Constants.COLOR_NEUTRAL;
        }
        switch (rating.toLowerCase()) {
            case Constants.RATING_SUPERSTAR:
                return Constants.COLOR_SUPERSTAR;
            case Constants.RATING_GOODIE:
                return Constants.COLOR_GOODIE;
            case Constants.RATING_NO_TAKE:
                return Constants.COLOR_NO_TAKE;
            default:
                return Constants.COLOR_NEUTRAL;
        }
    }
}
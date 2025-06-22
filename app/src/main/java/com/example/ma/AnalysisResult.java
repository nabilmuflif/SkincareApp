package com.example.ma;

import java.util.List;

public class AnalysisResult {
    private List<Ingredient> ingredients;
    private int superstarCount;
    private int goodieCount;
    private int noTakeCount; //
    private int neutralCount; //
    private double overallScore; //

    public AnalysisResult(List<Ingredient> ingredients) {
        this.ingredients = ingredients; //
        calculateCounts(); //
        calculateOverallScore(); //
    }

    private void calculateCounts() {
        superstarCount = 0; //
        goodieCount = 0; //
        noTakeCount = 0; //
        neutralCount = 0; //

        for (Ingredient ingredient : ingredients) {
            switch (ingredient.getRating().toLowerCase()) { //
                case Constants.RATING_SUPERSTAR: // Menggunakan konstanta
                    superstarCount++; //
                    break; //
                case Constants.RATING_GOODIE: // Menggunakan konstanta
                    goodieCount++; //
                    break; //
                case Constants.RATING_NO_TAKE: // Menggunakan konstanta
                    noTakeCount++; //
                    break; //
                default:
                    neutralCount++; //
            }
        }
    }

    private void calculateOverallScore() {
        if (ingredients.isEmpty()) { //
            overallScore = 0; //
            return; //
        }

        // Menggunakan konstanta untuk bobot skor
        double score = (superstarCount * Constants.SCORE_SUPERSTAR +
                goodieCount * Constants.SCORE_GOODIE +
                neutralCount * Constants.SCORE_NEUTRAL +
                noTakeCount * Constants.SCORE_NO_TAKE)
                / ingredients.size(); //
        overallScore = Math.round(score * 10.0) / 10.0; //
    }

    // Getters
    public List<Ingredient> getIngredients() { return ingredients; } //
    public int getSuperstarCount() { return superstarCount; } //
    public int getGoodieCount() { return goodieCount; } //
    public int getNoTakeCount() { return noTakeCount; } //
    public int getNeutralCount() { return neutralCount; } //
    public double getOverallScore() { return overallScore; } //

    public String getOverallRating() {
        // Menggunakan konstanta untuk ambang batas rating
        if (overallScore >= Constants.THRESHOLD_EXCELLENT) return Constants.RATING_LABEL_EXCELLENT; //
        else if (overallScore >= Constants.THRESHOLD_GOOD) return Constants.RATING_LABEL_GOOD; //
        else if (overallScore >= Constants.THRESHOLD_FAIR) return Constants.RATING_LABEL_FAIR; //
        else return Constants.RATING_LABEL_POOR; //
    }
}
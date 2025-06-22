package com.example.ma;

import android.graphics.Color;

// Kelas untuk menyimpan konstanta aplikasi
public final class Constants {

    // Prevent instantiation
    private Constants() {}

    // Rating strings
    public static final String RATING_SUPERSTAR = "superstar";
    public static final String RATING_GOODIE = "goodie";
    public static final String RATING_NO_TAKE = "no-take";
    public static final String RATING_NEUTRAL = "neutral";

    // Rating colors (these should ideally be fetched from R.color, but defining here for illustration)
    // For actual app, use ContextCompat.getColor(context, R.color.superstar_color)
    public static final int COLOR_SUPERSTAR = 0xFF4CAF50; // Green
    public static final int COLOR_GOODIE = 0xFF8BC34A;    // Light Green
    public static final int COLOR_NO_TAKE = 0xFFF44336;   // Red
    public static final int COLOR_NEUTRAL = 0xFF9E9E9E;   // Grey

    // Scoring weights
    public static final double SCORE_SUPERSTAR = 4.0;
    public static final double SCORE_GOODIE = 3.0;
    public static final double SCORE_NEUTRAL = 2.0;
    public static final double SCORE_NO_TAKE = 1.0;

    // Overall Rating Thresholds
    public static final double THRESHOLD_EXCELLENT = 3.5;
    public static final double THRESHOLD_GOOD = 2.5;
    public static final double THRESHOLD_FAIR = 1.5;

    // Overall Rating Labels
    public static final String RATING_LABEL_EXCELLENT = "Excellent";
    public static final String RATING_LABEL_GOOD = "Good";
    public static final String RATING_LABEL_FAIR = "Fair";
    public static final String RATING_LABEL_POOR = "Poor";

    // SharedPreferences Keys
    public static final String PREF_FIRST_RUN = "is_first_run";
    public static final String PREF_LAST_UPDATE = "last_update_timestamp";
}
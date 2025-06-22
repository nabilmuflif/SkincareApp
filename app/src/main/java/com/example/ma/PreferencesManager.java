package com.example.ma;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREF_NAME = "IncidecoderPrefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setFirstRun(boolean isFirstRun) {
        editor.putBoolean(Constants.PREF_FIRST_RUN, isFirstRun);
        editor.apply();
    }

    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(Constants.PREF_FIRST_RUN, true);
    }

    public void setLastUpdate(long timestamp) {
        editor.putLong(Constants.PREF_LAST_UPDATE, timestamp);
        editor.apply();
    }

    public long getLastUpdate() {
        return sharedPreferences.getLong(Constants.PREF_LAST_UPDATE, 0);
    }
}
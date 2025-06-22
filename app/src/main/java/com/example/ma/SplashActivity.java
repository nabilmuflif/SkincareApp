package com.example.ma;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2.5 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Sembunyikan ActionBar jika ada
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Handler untuk menunda dan memulai MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish(); // Tutup activity ini agar tidak bisa kembali
        }, SPLASH_DELAY);
    }
}
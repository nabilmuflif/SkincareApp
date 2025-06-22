package com.example.ma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

// Pastikan Anda mengimpor ContextCompat jika Anda menargetkan API level yang lebih rendah dari 23
import androidx.core.content.ContextCompat; // Tambahkan ini

public class DecodeIngredientsActivity extends AppCompatActivity {

    private EditText etIngredientsList;
    private Button btnAnalyze, btnClear;
    private TextView tvAnalysisTitle, tvOverallScore, tvSuperstarCount, tvGoodieCount,
            tvNoTakeCount, tvNeutralCount;
    private RecyclerView rvAnalysisResults;
    private View layoutAnalysisStats;
    private ProgressBar progressBar;

    private DatabaseHelper dbHelper;
    private AnalysisResultAdapter analysisAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_ingredients);

        initializeViews();
        setupDatabase();
        setupClickListeners();
        setupRecyclerView();
        handleIntentExtras();
    }

    private void initializeViews() {
        etIngredientsList = findViewById(R.id.etIngredientsList);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        btnClear = findViewById(R.id.btnClear);
        tvAnalysisTitle = findViewById(R.id.tvAnalysisTitle);
        tvOverallScore = findViewById(R.id.tvOverallScore);
        tvSuperstarCount = findViewById(R.id.tvSuperstarCount);
        tvGoodieCount = findViewById(R.id.tvGoodieCount);
        tvNoTakeCount = findViewById(R.id.tvNoTakeCount);
        tvNeutralCount = findViewById(R.id.tvNeutralCount);
        rvAnalysisResults = findViewById(R.id.rvAnalysisResults);
        layoutAnalysisStats = findViewById(R.id.layoutAnalysisStats);
        progressBar = findViewById(R.id.progressBar);

        hideAnalysisResults();
    }

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        btnAnalyze.setOnClickListener(v -> analyzeIngredients());
        btnClear.setOnClickListener(v -> clearAnalysis());
    }

    private void setupRecyclerView() {
        rvAnalysisResults.setLayoutManager(new LinearLayoutManager(this));
        analysisAdapter = new AnalysisResultAdapter(new ArrayList<>(), this::onIngredientClick);
        rvAnalysisResults.setAdapter(analysisAdapter);
    }

    private void handleIntentExtras() {
        Intent intent = getIntent();
        if (intent.hasExtra("ingredients")) {
            String ingredients = intent.getStringExtra("ingredients");
            if (ingredients != null && !ingredients.isEmpty()) {
                etIngredientsList.setText(ingredients);
                analyzeIngredients();
            }
        }
    }

    private void analyzeIngredients() {
        String ingredientsText = etIngredientsList.getText().toString().trim();
        if (ingredientsText.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_ingredients_message), Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        new Thread(() -> {
            AnalysisResult result = dbHelper.analyzeIngredients(ingredientsText);
            runOnUiThread(() -> {
                displayAnalysisResults(result);
                showLoading(false);
            });
        }).start();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnAnalyze.setEnabled(!show);
        btnAnalyze.setText(show ? getString(R.string.analyzing) : getString(R.string.analyze_button));
        btnClear.setEnabled(!show);
    }

    private void displayAnalysisResults(AnalysisResult result) {
        showAnalysisResults();

        tvAnalysisTitle.setText(getString(R.string.analysis_results) + " (" + result.getIngredients().size() + " ingredients)");
        tvOverallScore.setText(String.format("%.1f/4.0 (%s)", result.getOverallScore(), result.getOverallRating()));
        tvSuperstarCount.setText(String.valueOf(result.getSuperstarCount()));
        tvGoodieCount.setText(String.valueOf(result.getGoodieCount()));
        tvNoTakeCount.setText(String.valueOf(result.getNoTakeCount()));
        tvNeutralCount.setText(String.valueOf(result.getNeutralCount()));

        setOverallScoreColor(result.getOverallScore());

        analysisAdapter = new AnalysisResultAdapter(result.getIngredients(), this::onIngredientClick);
        rvAnalysisResults.setAdapter(analysisAdapter);
        rvAnalysisResults.smoothScrollToPosition(0);
    }

    private void setOverallScoreColor(double score) {
        int color;
        if (score >= Constants.THRESHOLD_EXCELLENT) {
            color = ContextCompat.getColor(this, R.color.superstar_color); // Gunakan R.color.nama_warna
        } else if (score >= Constants.THRESHOLD_GOOD) {
            color = ContextCompat.getColor(this, R.color.goodie_color); // Gunakan R.color.nama_warna
        } else if (score >= Constants.THRESHOLD_FAIR) {
            color = ContextCompat.getColor(this, R.color.warning_color); // Warna oranye untuk "Fair" (Anda menggunakan no_take_color sebelumnya, warning_color lebih tepat)
        } else {
            color = ContextCompat.getColor(this, R.color.no_take_color); // Warna merah untuk "Poor" (Tetap pakai no_take_color atau error_color)
        }
        tvOverallScore.setTextColor(color);
    }

    private void showAnalysisResults() {
        layoutAnalysisStats.setVisibility(View.VISIBLE);
        tvAnalysisTitle.setVisibility(View.VISIBLE);
        rvAnalysisResults.setVisibility(View.VISIBLE);
    }

    private void hideAnalysisResults() {
        layoutAnalysisStats.setVisibility(View.GONE);
        tvAnalysisTitle.setVisibility(View.GONE);
        rvAnalysisResults.setVisibility(View.GONE);
    }

    private void clearAnalysis() {
        etIngredientsList.setText("");
        hideAnalysisResults();
        Toast.makeText(this, getString(R.string.analysis_cleared), Toast.LENGTH_SHORT).show();
    }

    private void onIngredientClick(Ingredient ingredient) {
        showIngredientDetailDialog(ingredient);
    }

    private void showIngredientDetailDialog(Ingredient ingredient) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ingredient_detail, null);
        TextView tvName = dialogView.findViewById(R.id.tvIngredientName);
        TextView tvCommonName = dialogView.findViewById(R.id.tvCommonName);
        TextView tvRating = dialogView.findViewById(R.id.tvRating);
        TextView tvWhatItDoes = dialogView.findViewById(R.id.tvWhatItDoes);
        TextView tvDescription = dialogView.findViewById(R.id.tvDescription);
        TextView tvIrritancy = dialogView.findViewById(R.id.tvIrritancy);
        View vRatingBadge = dialogView.findViewById(R.id.vRatingBadge);

        tvName.setText(ingredient.getName());
        tvCommonName.setText(ingredient.getCommonName());
        tvRating.setText(ingredient.getRating().toUpperCase());
        tvWhatItDoes.setText(getString(R.string.what_it_does) + " " + ingredient.getWhatItDoes());
        tvDescription.setText(ingredient.getDescription());
        tvIrritancy.setText(getString(R.string.irritancy_level) + " " + ingredient.getIrritancyLevel());
        int color = ingredient.getRatingColor();
        vRatingBadge.setBackgroundColor(color);
        tvRating.setTextColor(android.graphics.Color.WHITE);
        builder.setView(dialogView)
                .setPositiveButton(getString(R.string.close), null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
package com.example.ma;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngredientFragment extends Fragment {

    private RecyclerView rvAllIngredients;
    private ProgressBar progressBar;
    private IngredientAdapter ingredientAdapter;
    private DatabaseHelper dbHelper;
    private ExecutorService executorService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        rvAllIngredients = view.findViewById(R.id.rvAllIngredients);
        rvAllIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        loadAllIngredients();

        return view;
    }

    private void loadAllIngredients() {
        showLoading(true);
        executorService.execute(() -> {
            List<Ingredient> allIngredients = dbHelper.getAllIngredients();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // IMPLEMENTASI LISTENER YANG BENAR
                    ingredientAdapter = new IngredientAdapter(allIngredients, new IngredientAdapter.OnIngredientClickListener() {
                        @Override
                        public void onIngredientClick(Ingredient ingredient) {
                            // Aksi saat item di-klik
                            showIngredientDetailDialog(ingredient);
                        }

                        @Override
                        public void onFavoriteClick(Ingredient ingredient, int position) {
                            // Aksi saat tombol favorit di-klik
                            boolean newStatus = !ingredient.isFavorite();
                            dbHelper.setIngredientFavorite(ingredient.getId(), newStatus);
                            ingredient.setFavorite(newStatus);
                            ingredientAdapter.notifyItemChanged(position); // Update tampilan item
                        }
                    });
                    rvAllIngredients.setAdapter(ingredientAdapter);
                    showLoading(false);
                });
            }
        });
    }

    private void showIngredientDetailDialog(Ingredient ingredient) {
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        // Anda mungkin ingin mengubah warna teks rating di sini juga jika perlu
        // tvRating.setTextColor(Color.WHITE);

        builder.setView(dialogView)
                .setPositiveButton(getString(R.string.close), null)
                .create()
                .show();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rvAllIngredients.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
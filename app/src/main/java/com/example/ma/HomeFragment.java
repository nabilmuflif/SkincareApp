package com.example.ma;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private EditText etSearch;
    private Button btnSearch, btnDecodeIngredients;
    private RecyclerView rvProducts, rvIngredients;
    private ProgressBar progressBar;
    private TextView tvRecentProductsTitle, tvIngredientsTitle;
    private Button btnAddIngredient;
    private Button btnAddSkincare;

    private DatabaseHelper dbHelper;
    private ProductAdapter productAdapter;
    private IngredientAdapter ingredientAdapter;
    private ExecutorService executorService;

    private List<Product> productList = new ArrayList<>();
    private List<Ingredient> ingredientList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
        executorService = Executors.newFixedThreadPool(2);
        productAdapter = new ProductAdapter(productList, this::onProductClick);
        ingredientAdapter = new IngredientAdapter(ingredientList, this::onIngredientClick);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupRecyclerViews();
        setupClickListeners();
        loadRecentProducts();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ... (inisialisasi variabel yang sudah ada)

        // Inisialisasi tombol baru
        btnAddIngredient = view.findViewById(R.id.btnAddIngredient);
        btnAddSkincare = view.findViewById(R.id.btnAddSkincare);

        // ... (listener untuk tombol search dan decode)

        // Listener untuk tombol "Tambah Bahan"
        btnAddIngredient.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur 'Tambah Bahan' akan segera hadir!", Toast.LENGTH_SHORT).show();
        });

        // Listener untuk tombol "Tambah Skincare"
        btnAddSkincare.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur 'Tambah Skincare' akan segera hadir!", Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnDecodeIngredients = view.findViewById(R.id.btnDecodeIngredients);
        rvProducts = view.findViewById(R.id.rvProducts);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        progressBar = view.findViewById(R.id.progressBar);
        tvRecentProductsTitle = view.findViewById(R.id.tvRecentProductsTitle);
        tvIngredientsTitle = view.findViewById(R.id.tvIngredientsTitle);
    }

    private void setupRecyclerViews() {
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(productAdapter);
        rvProducts.setNestedScrollingEnabled(false);

        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIngredients.setAdapter(ingredientAdapter);
        rvIngredients.setNestedScrollingEnabled(false);
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> performSearch());
        btnDecodeIngredients.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DecodeIngredientsActivity.class);
            startActivity(intent);
        });
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(getContext(), getString(R.string.search_empty_hint), Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        tvRecentProductsTitle.setText("Product Results");
        tvIngredientsTitle.setVisibility(View.VISIBLE);
        rvIngredients.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            List<Product> searchResults = dbHelper.searchProducts(query);
            List<Ingredient> ingredientResults = dbHelper.searchIngredients(query);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    productList.clear();
                    productList.addAll(searchResults);
                    productAdapter.notifyDataSetChanged();

                    ingredientList.clear();
                    ingredientList.addAll(ingredientResults);
                    ingredientAdapter.notifyDataSetChanged();

                    showLoading(false);
                    if (searchResults.isEmpty() && ingredientResults.isEmpty()) {
                        Toast.makeText(getContext(), getString(R.string.no_results_found), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadRecentProducts() {
        showLoading(true);
        tvRecentProductsTitle.setText("Recent Products");
        tvIngredientsTitle.setVisibility(View.GONE);
        rvIngredients.setVisibility(View.GONE);

        executorService.execute(() -> {
            List<Product> recentProducts = dbHelper.getRecentProducts(5);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    productList.clear();
                    productList.addAll(recentProducts);
                    productAdapter.notifyDataSetChanged();
                    showLoading(false);
                });
            }
        });
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(getActivity(), DecodeIngredientsActivity.class);
        intent.putExtra("ingredients", product.getIngredients());
        startActivity(intent);
    }

    private void onIngredientClick(Ingredient ingredient) {
        showIngredientDetailDialog(ingredient);
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
        tvRating.setTextColor(android.graphics.Color.WHITE);

        builder.setView(dialogView)
                .setPositiveButton(getString(R.string.close), null)
                .create()
                .show();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSearch.setEnabled(!show);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
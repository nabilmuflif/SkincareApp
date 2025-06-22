package com.example.ma;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout; // Import LinearLayout
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
    // Tipe data btnAddIngredient dan btnAddSkincare diubah
    private View btnAddIngredient, btnAddSkincare;
    private Button btnSearch, btnDecodeIngredients;
    private RecyclerView rvProducts, rvIngredients;
    private ProgressBar progressBar;
    private TextView tvRecentProductsTitle, tvIngredientsTitle;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerViews();
        setupAdapters();
        setupClickListeners();
        loadRecentProducts();
    }

    private void initializeViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnDecodeIngredients = view.findViewById(R.id.btnDecodeIngredients);
        // ID yang dipanggil sudah benar, tidak perlu diubah, hanya tipe variabelnya
        btnAddIngredient = view.findViewById(R.id.btnAddIngredient);
        btnAddSkincare = view.findViewById(R.id.btnAddSkincare);
        rvProducts = view.findViewById(R.id.rvProducts);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        progressBar = view.findViewById(R.id.progressBar);
        tvRecentProductsTitle = view.findViewById(R.id.tvRecentProductsTitle);
        tvIngredientsTitle = view.findViewById(R.id.tvIngredientsTitle);
    }

    private void setupRecyclerViews() {
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setNestedScrollingEnabled(false);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIngredients.setNestedScrollingEnabled(false);
    }

    private void setupAdapters() {
        productAdapter = new ProductAdapter(productList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(getActivity(), DecodeIngredientsActivity.class);
                intent.putExtra("ingredients", product.getIngredients());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Product product, int position) {
                boolean newStatus = !product.isFavorite();
                dbHelper.setProductFavorite(product.getId(), newStatus);
                product.setFavorite(newStatus);
                productAdapter.notifyItemChanged(position);
            }
        });
        rvProducts.setAdapter(productAdapter);

        ingredientAdapter = new IngredientAdapter(ingredientList, new IngredientAdapter.OnIngredientClickListener() {
            @Override
            public void onIngredientClick(Ingredient ingredient) {
                showIngredientDetailDialog(ingredient);
            }

            @Override
            public void onFavoriteClick(Ingredient ingredient, int position) {
                boolean newStatus = !ingredient.isFavorite();
                dbHelper.setIngredientFavorite(ingredient.getId(), newStatus);
                ingredient.setFavorite(newStatus);
                ingredientAdapter.notifyItemChanged(position);
            }
        });
        rvIngredients.setAdapter(ingredientAdapter);
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> performSearch());
        btnDecodeIngredients.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DecodeIngredientsActivity.class);
            startActivity(intent);
        });
        // Listener untuk LinearLayout yang sekarang disimpan dalam variabel View
        btnAddSkincare.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddProductActivity.class));
        });
        btnAddIngredient.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddIngredientActivity.class));
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
            List<Product> searchResultsProducts = dbHelper.searchProducts(query);
            List<Ingredient> searchResultsIngredients = dbHelper.searchIngredients(query);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    productList.clear();
                    productList.addAll(searchResultsProducts);
                    productAdapter.notifyDataSetChanged();

                    ingredientList.clear();
                    ingredientList.addAll(searchResultsIngredients);
                    ingredientAdapter.notifyDataSetChanged();

                    showLoading(false);
                    if (searchResultsProducts.isEmpty() && searchResultsIngredients.isEmpty()) {
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

    private void showIngredientDetailDialog(Ingredient ingredient) {
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ingredient_detail, null);

        TextView tvName = dialogView.findViewById(R.id.tvIngredientName);
        // ... (dan view lainnya di dialog)

        tvName.setText(ingredient.getName());
        // ... (set view lainnya)

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
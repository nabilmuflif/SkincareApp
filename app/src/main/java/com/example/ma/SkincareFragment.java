package com.example.ma;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkincareFragment extends Fragment {

    private RecyclerView rvAllProducts;
    private ProgressBar progressBar;
    private ProductAdapter productAdapter;
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
        View view = inflater.inflate(R.layout.fragment_skincare, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        rvAllProducts = view.findViewById(R.id.rvAllProducts);
        rvAllProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        loadAllProducts();

        return view;
    }

    private void loadAllProducts() {
        showLoading(true);
        executorService.execute(() -> {
            // Anda perlu menambahkan method getAllProducts() di DatabaseHelper
            List<Product> allProducts = dbHelper.getAllProducts();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    productAdapter = new ProductAdapter(allProducts, this::onProductClick);
                    rvAllProducts.setAdapter(productAdapter);
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

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rvAllProducts.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
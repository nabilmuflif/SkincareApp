package com.example.ma;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavProducts, rvFavIngredients;
    private TextView tvEmpty, titleProducts, titleIngredients;
    private DatabaseHelper dbHelper;
    private ProductAdapter productAdapter;
    private IngredientAdapter ingredientAdapter;
    private List<Product> favoriteProducts;
    private List<Ingredient> favoriteIngredients;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        dbHelper = new DatabaseHelper(getContext());

        rvFavProducts = view.findViewById(R.id.rvFavoriteProducts);
        rvFavIngredients = view.findViewById(R.id.rvFavoriteIngredients);
        tvEmpty = view.findViewById(R.id.tvEmptyFavorites);
        titleProducts = view.findViewById(R.id.titleFavoriteProducts);
        titleIngredients = view.findViewById(R.id.titleFavoriteIngredients);

        rvFavProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        favoriteProducts = dbHelper.getFavoriteProducts();
        favoriteIngredients = dbHelper.getFavoriteIngredients();

        // Cek apakah ada data favorit
        if (favoriteProducts.isEmpty() && favoriteIngredients.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFavProducts.setVisibility(View.GONE);
            rvFavIngredients.setVisibility(View.GONE);
            titleProducts.setVisibility(View.GONE);
            titleIngredients.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);

            // Tampilkan atau sembunyikan list produk
            if (favoriteProducts.isEmpty()) {
                rvFavProducts.setVisibility(View.GONE);
                titleProducts.setVisibility(View.GONE);
            } else {
                rvFavProducts.setVisibility(View.VISIBLE);
                titleProducts.setVisibility(View.VISIBLE);
                setupProductAdapter();
            }

            // Tampilkan atau sembunyikan list bahan
            if (favoriteIngredients.isEmpty()) {
                rvFavIngredients.setVisibility(View.GONE);
                titleIngredients.setVisibility(View.GONE);
            } else {
                rvFavIngredients.setVisibility(View.VISIBLE);
                titleIngredients.setVisibility(View.VISIBLE);
                setupIngredientAdapter();
            }
        }
    }

    private void setupProductAdapter() {
        productAdapter = new ProductAdapter(favoriteProducts, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(getActivity(), DecodeIngredientsActivity.class);
                intent.putExtra("ingredients", product.getIngredients());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Product product, int position) {
                dbHelper.setProductFavorite(product.getId(), false); // Selalu set ke false
                favoriteProducts.remove(position);
                productAdapter.notifyItemRemoved(position);
                productAdapter.notifyItemRangeChanged(position, favoriteProducts.size());

                // Cek ulang apakah semua favorit sudah kosong
                if (favoriteProducts.isEmpty() && favoriteIngredients.isEmpty()) {
                    loadFavorites();
                } else if (favoriteProducts.isEmpty()) {
                    rvFavProducts.setVisibility(View.GONE);
                    titleProducts.setVisibility(View.GONE);
                }
            }
        });
        rvFavProducts.setAdapter(productAdapter);
    }

    private void setupIngredientAdapter() {
        // Implementasi serupa untuk IngredientAdapter
        ingredientAdapter = new IngredientAdapter(favoriteIngredients, new IngredientAdapter.OnIngredientClickListener() {
            @Override
            public void onIngredientClick(Ingredient ingredient) {
                // Tampilkan dialog detail bahan
            }
            @Override
            public void onFavoriteClick(Ingredient ingredient, int position) {
                dbHelper.setIngredientFavorite(ingredient.getId(), false);
                favoriteIngredients.remove(position);
                ingredientAdapter.notifyItemRemoved(position);
                ingredientAdapter.notifyItemRangeChanged(position, favoriteIngredients.size());

                if (favoriteProducts.isEmpty() && favoriteIngredients.isEmpty()) {
                    loadFavorites();
                } else if (favoriteIngredients.isEmpty()) {
                    rvFavIngredients.setVisibility(View.GONE);
                    titleIngredients.setVisibility(View.GONE);
                }
            }
        });
        rvFavIngredients.setAdapter(ingredientAdapter);
    }
}
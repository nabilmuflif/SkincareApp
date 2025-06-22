package com.example.ma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private final List<Ingredient> ingredients;
    private final OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    public IngredientAdapter(List<Ingredient> ingredients, OnIngredientClickListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient, listener);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        // Deklarasikan view sesuai dengan ID di layout XML yang baru
        private final TextView tvIngredientName;
        private final TextView tvWhatItDoes;
        private final View vRatingIndicator;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi view menggunakan ID yang benar
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvWhatItDoes = itemView.findViewById(R.id.tv_ingredient_function);
            vRatingIndicator = itemView.findViewById(R.id.v_rating_indicator);
        }

        public void bind(Ingredient ingredient, final OnIngredientClickListener listener) {
            // Cek null untuk keamanan, meskipun seharusnya tidak terjadi jika layout benar
            if (tvIngredientName != null) {
                tvIngredientName.setText(ingredient.getName());
            }
            if (tvWhatItDoes != null) {
                tvWhatItDoes.setText(ingredient.getWhatItDoes());
            }

            // Atur warna indikator berdasarkan rating
            if (vRatingIndicator != null) {
                vRatingIndicator.setBackgroundColor(ingredient.getRatingColor());
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });
        }
    }
}
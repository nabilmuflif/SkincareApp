package com.example.ma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AnalysisResultAdapter extends RecyclerView.Adapter<AnalysisResultAdapter.AnalysisViewHolder> {

    private List<Ingredient> ingredients;
    private OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    public AnalysisResultAdapter(List<Ingredient> ingredients, OnIngredientClickListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_analysis_result, parent, false);
        return new AnalysisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalysisViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient, listener);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // --- KELAS VIEWHOLDER YANG DIPERBAIKI ---
    static class AnalysisViewHolder extends RecyclerView.ViewHolder {
        // Deklarasi variabel view tetap sama
        private TextView tvIngredientName, tvRating, tvWhatItDoes;
        private View vRatingIndicator;

        public AnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi view dengan ID yang BENAR dari file XML
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvRating = itemView.findViewById(R.id.tv_rating_value); // ID di XML adalah tv_rating_value
            tvWhatItDoes = itemView.findViewById(R.id.tv_ingredient_function); // ID di XML adalah tv_ingredient_function
            vRatingIndicator = itemView.findViewById(R.id.v_rating_indicator); // ID di XML adalah v_rating_indicator
        }

        public void bind(final Ingredient ingredient, final OnIngredientClickListener listener) {
            tvIngredientName.setText(ingredient.getName());
            tvRating.setText(ingredient.getRating().toUpperCase());
            tvWhatItDoes.setText(ingredient.getWhatItDoes());

            int color = ingredient.getRatingColor();
            // Anda mungkin perlu menyesuaikan TextView mana yang diwarnai.
            // Berdasarkan layout, sepertinya tvRating (tv_rating_value) yang seharusnya diwarnai.
            tvRating.setTextColor(color);

            vRatingIndicator.setBackgroundColor(color);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });
        }
    }
}
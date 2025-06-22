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

    static class AnalysisViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIngredientName, tvRating, tvWhatItDoes;
        private View vRatingIndicator;

        public AnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvWhatItDoes = itemView.findViewById(R.id.tvWhatItDoes);
            vRatingIndicator = itemView.findViewById(R.id.vRatingIndicator);
        }

        public void bind(Ingredient ingredient, OnIngredientClickListener listener) {
            tvIngredientName.setText(ingredient.getName());
            tvRating.setText(ingredient.getRating().toUpperCase());
            tvWhatItDoes.setText(ingredient.getWhatItDoes());

            int color = ingredient.getRatingColor();
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
package com.example.ma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton; // Import MaterialButton
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private final List<Ingredient> ingredients;
    private final OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
        void onFavoriteClick(Ingredient ingredient, int position);
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
        holder.bind(ingredient, listener, position);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvIngredientName;
        private final TextView tvWhatItDoes;
        private final View vRatingIndicator;
        // Tipe data diubah menjadi MaterialButton sesuai dengan layout XML
        private final MaterialButton btnFavorite;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvWhatItDoes = itemView.findViewById(R.id.tv_ingredient_function);
            vRatingIndicator = itemView.findViewById(R.id.v_rating_indicator);
            // ID diperbaiki agar sesuai dengan XML (btn_favorite_ingredient)
            btnFavorite = itemView.findViewById(R.id.btn_favorite_ingredient);
        }

        public void bind(final Ingredient ingredient, final OnIngredientClickListener listener, final int position) {
            tvIngredientName.setText(ingredient.getName());
            tvWhatItDoes.setText(ingredient.getWhatItDoes());
            vRatingIndicator.setBackgroundColor(ingredient.getRatingColor());

            if (btnFavorite != null) {
                // Untuk MaterialButton, kita tidak menggunakan setSelected.
                // Kita bisa mengubah tint ikonnya jika perlu, tapi untuk sekarang kita biarkan.
                // Logika favorit akan di-handle oleh klik.
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });

            if (btnFavorite != null) {
                btnFavorite.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onFavoriteClick(ingredient, position);
                    }
                });
            }
        }
    }
}
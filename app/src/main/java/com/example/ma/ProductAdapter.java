package com.example.ma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnProductClickListener listener;

    // INTERFACE DIPERBARUI: Tambahkan onFavoriteClick
    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product, int position);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener, position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName, tvBrand;
        private ImageButton btnFavorite; // Tambahkan ImageButton

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            btnFavorite = itemView.findViewById(R.id.btnFavoriteProduct); // Pastikan ID ini ada di XML
        }

        public void bind(final Product product, final OnProductClickListener listener, final int position) {
            tvProductName.setText(product.getName());
            tvBrand.setText(product.getBrand());

            // Set status favorit dari data model
            if (btnFavorite != null) {
                btnFavorite.setSelected(product.isFavorite());
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            // Listener untuk tombol favorit
            if (btnFavorite != null) {
                btnFavorite.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onFavoriteClick(product, position);
                    }
                });
            }
        }
    }
}
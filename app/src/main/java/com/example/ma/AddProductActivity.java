package com.example.ma;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class AddProductActivity extends AppCompatActivity {
    private TextInputEditText etName, etBrand, etCategory, etIngredients;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.etAddProductName);
        etBrand = findViewById(R.id.etAddProductBrand);
        etCategory = findViewById(R.id.etAddProductCategory);
        etIngredients = findViewById(R.id.etAddProductIngredients);
        Button btnSave = findViewById(R.id.btnSaveProduct);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String brand = etBrand.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();

        if (name.isEmpty() || brand.isEmpty()) {
            Toast.makeText(this, "Nama dan Merek tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        Product newProduct = new Product(name, brand, ingredients, category);
        long id = dbHelper.addProduct(newProduct);

        if (id != -1) {
            Toast.makeText(this, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke halaman sebelumnya
        } else {
            Toast.makeText(this, "Gagal menambahkan produk.", Toast.LENGTH_SHORT).show();
        }
    }
}
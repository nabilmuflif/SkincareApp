package com.example.ma;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {
    private EditText etName, etCommonName, etRating, etWhatItDoes, etDescription, etIrritancy;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);
        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etAddIngredientName);
        etCommonName = findViewById(R.id.etAddIngredientCommonName);
        etRating = findViewById(R.id.etAddIngredientRating);
        etWhatItDoes = findViewById(R.id.etAddIngredientWhatItDoes);
        etDescription = findViewById(R.id.etAddIngredientDescription);
        etIrritancy = findViewById(R.id.etAddIngredientIrritancy);
        Button btnSave = findViewById(R.id.btnSaveIngredient);

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String commonName = etCommonName.getText().toString().trim();
        String rating = etRating.getText().toString().trim();
        String whatItDoes = etWhatItDoes.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String irritancy = etIrritancy.getText().toString().trim();

        if (name.isEmpty() || rating.isEmpty()) {
            Toast.makeText(this, "Nama dan Rating tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        Ingredient newIngredient = new Ingredient(name, commonName, rating, whatItDoes, description, irritancy);
        long id = dbHelper.addIngredient(newIngredient);

        if (id != -1) {
            Toast.makeText(this, "Bahan berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menambahkan bahan.", Toast.LENGTH_SHORT).show();
        }
    }
}
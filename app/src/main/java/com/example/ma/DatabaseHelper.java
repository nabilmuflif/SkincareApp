package com.example.ma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "incidecoder.db";
    private static final int DATABASE_VERSION = 2; // VERSI DATABASE DINAIKKAN

    // Nama Tabel
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_INGREDIENTS = "ingredients";

    // Kolom Umum
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_IS_FAVORITE = "is_favorite";

    // Kolom Tabel Produk
    private static final String PRODUCT_BRAND = "brand";
    private static final String PRODUCT_INGREDIENTS = "ingredients";
    private static final String PRODUCT_CATEGORY = "category";
    private static final String PRODUCT_CREATED_AT = "created_at";

    // Kolom Tabel Bahan
    private static final String INGREDIENT_COMMON_NAME = "common_name";
    private static final String INGREDIENT_RATING = "rating";
    private static final String INGREDIENT_WHAT_IT_DOES = "what_it_does";
    private static final String INGREDIENT_DESCRIPTION = "description";
    private static final String INGREDIENT_IRRITANCY = "irritancy_level";
    private static final String INGREDIENT_SOURCES = "sources";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat Tabel Produk dengan kolom favorit
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT NOT NULL," +
                PRODUCT_BRAND + " TEXT NOT NULL," +
                PRODUCT_INGREDIENTS + " TEXT," +
                PRODUCT_CATEGORY + " TEXT," +
                COL_IS_FAVORITE + " INTEGER DEFAULT 0," + // Kolom baru
                PRODUCT_CREATED_AT + " INTEGER" + ")";

        // Membuat Tabel Bahan dengan kolom favorit
        String createIngredientsTable = "CREATE TABLE " + TABLE_INGREDIENTS + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT UNIQUE NOT NULL," +
                INGREDIENT_COMMON_NAME + " TEXT," +
                INGREDIENT_RATING + " TEXT," +
                INGREDIENT_WHAT_IT_DOES + " TEXT," +
                INGREDIENT_DESCRIPTION + " TEXT," +
                INGREDIENT_IRRITANCY + " TEXT," +
                COL_IS_FAVORITE + " INTEGER DEFAULT 0," + // Kolom baru
                INGREDIENT_SOURCES + " TEXT" + ")";

        db.execSQL(createProductsTable);
        db.execSQL(createIngredientsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Strategi upgrade sederhana: hapus tabel lama dan buat yang baru
        // Catatan: Ini akan menghapus semua data pengguna saat aplikasi diupgrade.
        // Untuk aplikasi produksi, diperlukan skema migrasi yang lebih baik.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        onCreate(db);
    }

    // --- METODE PRODUK ---

    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, product.getName());
        values.put(PRODUCT_BRAND, product.getBrand());
        values.put(PRODUCT_INGREDIENTS, product.getIngredients());
        values.put(PRODUCT_CATEGORY, product.getCategory());
        values.put(PRODUCT_CREATED_AT, System.currentTimeMillis());
        values.put(COL_IS_FAVORITE, product.isFavorite() ? 1 : 0);
        long id = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return id;
    }

    public List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_NAME + " LIKE ? OR " + PRODUCT_BRAND + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%"};
        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection, selectionArgs, null, null, PRODUCT_CREATED_AT + " DESC");
        if (cursor.moveToFirst()) {
            do {
                products.add(cursorToProduct(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return products;
    }

    public List<Product> getRecentProducts(int limit) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null,
                PRODUCT_CREATED_AT + " DESC", String.valueOf(limit));
        if (cursor.moveToFirst()) {
            do {
                products.add(cursorToProduct(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return products;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, COL_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                products.add(cursorToProduct(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return products;
    }

    // --- METODE BAHAN ---

    public long addIngredient(Ingredient ingredient) {
        if (!isValidIngredient(ingredient)) {
            Log.e(TAG, "Data bahan tidak valid.");
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, ingredient.getName());
        values.put(INGREDIENT_COMMON_NAME, ingredient.getCommonName());
        values.put(INGREDIENT_RATING, ingredient.getRating());
        values.put(INGREDIENT_WHAT_IT_DOES, ingredient.getWhatItDoes());
        values.put(INGREDIENT_DESCRIPTION, ingredient.getDescription());
        values.put(INGREDIENT_IRRITANCY, ingredient.getIrritancyLevel());
        values.put(INGREDIENT_SOURCES, ingredient.getSources());
        values.put(COL_IS_FAVORITE, ingredient.isFavorite() ? 1 : 0);
        long id = db.insertWithOnConflict(TABLE_INGREDIENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    public List<Ingredient> searchIngredients(String query) {
        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_NAME + " LIKE ? OR " + INGREDIENT_COMMON_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%"};
        Cursor cursor = db.query(TABLE_INGREDIENTS, null, selection, selectionArgs, null, null, COL_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                ingredients.add(cursorToIngredient(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredients;
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INGREDIENTS, null, null, null, null, null, COL_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                ingredients.add(cursorToIngredient(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredients;
    }

    public Ingredient getIngredientByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INGREDIENTS, null, COL_NAME + " = ?", new String[]{name.trim()}, null, null, null);
        Ingredient ingredient = null;
        if (cursor.moveToFirst()) {
            ingredient = cursorToIngredient(cursor);
        }
        cursor.close();
        db.close();
        return ingredient;
    }

    // --- METODE UNTUK FITUR FAVORIT ---

    public void setProductFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_PRODUCTS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void setIngredientFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_INGREDIENTS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Product> getFavoriteProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COL_IS_FAVORITE + " = 1", null, null, null, PRODUCT_CREATED_AT + " DESC");
        if (cursor.moveToFirst()) {
            do {
                products.add(cursorToProduct(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return products;
    }

    public List<Ingredient> getFavoriteIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INGREDIENTS, null, COL_IS_FAVORITE + " = 1", null, null, null, COL_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                ingredients.add(cursorToIngredient(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredients;
    }

    // --- METODE HELPER ---

    private Product cursorToProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        product.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_BRAND)));
        product.setIngredients(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_INGREDIENTS)));
        product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_CATEGORY)));
        product.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(PRODUCT_CREATED_AT)));
        product.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FAVORITE)) == 1);
        return product;
    }

    private Ingredient cursorToIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        ingredient.setCommonName(cursor.getString(cursor.getColumnIndexOrThrow(INGREDIENT_COMMON_NAME)));
        ingredient.setRating(cursor.getString(cursor.getColumnIndexOrThrow(INGREDIENT_RATING)));
        ingredient.setWhatItDoes(cursor.getString(cursor.getColumnIndexOrThrow(INGREDIENT_WHAT_IT_DOES)));
        ingredient.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(INGREDIENT_DESCRIPTION)));
        ingredient.setIrritancyLevel(cursor.getString(cursor.getColumnIndexOrThrow(INGREDIENT_IRRITANCY)));
        ingredient.setSources(cursor.getString(cursor.getColumnIndexOrThrow(INGREDIENT_SOURCES)));
        ingredient.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FAVORITE)) == 1);
        return ingredient;
    }

    private boolean isValidIngredient(Ingredient ingredient) {
        return ingredient != null && !TextUtils.isEmpty(ingredient.getName()) && !TextUtils.isEmpty(ingredient.getRating());
    }

    // --- Inisialisasi Data Awal ---

    public void initializeDefaultData() {
        if (isDatabaseEmpty()) {
            addDefaultIngredients();
            addDefaultProducts();
        }
    }

    private boolean isDatabaseEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }
        return count == 0;
    }

    private void addDefaultIngredients() {
        Ingredient[] defaultIngredients = {
                new Ingredient("Retinyl Palmitate", "Vitamin A Palmitate", Constants.RATING_NO_TAKE, "cell-communicating ingredient", "A form of vitamin A that can be irritating and may cause photosensitivity.", "medium"),
                new Ingredient("Azelaic Acid", "Azelaic Acid", Constants.RATING_SUPERSTAR, "anti-acne, soothing, buffering", "A gentle exfoliant that helps with acne and brightens skin.", "low"),
                new Ingredient("Ascorbyl Glucoside", "Vitamin C Derivative", Constants.RATING_GOODIE, "antioxidant, skin brightening", "A stable form of Vitamin C that provides antioxidant benefits.", "low"),
                new Ingredient("Ceramide EOP", "Ceramide EOP", Constants.RATING_GOODIE, "skin-identical ingredient", "Helps restore and maintain the skin barrier.", "low"),
                new Ingredient("Niacinamide", "Vitamin B3", Constants.RATING_SUPERSTAR, "anti-acne, skin brightening, anti-aging", "A versatile ingredient that helps with multiple skin concerns.", "low")
        };
        for (Ingredient ingredient : defaultIngredients) {
            addIngredient(ingredient);
        }
    }

    private void addDefaultProducts() {
        Product[] defaultProducts = {
                new Product("Clear Skin Oil Balancing Moisturiser", "Simple", "Aqua, Niacinamide, Glycerin, Azelaic Acid, Ceramide EOP", "Moisturizer"),
                new Product("Omega + Complex Eye Cream", "Paula's Choice", "Aqua, Retinyl Palmitate, Ascorbyl Glucoside, Ceramide EOP", "Eye Cream"),
                new Product("Vitamin C Paste", "Lixir", "Ascorbyl Glucoside, Niacinamide, Glycerin", "Serum")
        };
        for (Product product : defaultProducts) {
            addProduct(product);
        }
    }

    // --- Metode Analisis Bahan ---

    public AnalysisResult analyzeIngredients(String ingredientText) {
        List<Ingredient> foundIngredients = new ArrayList<>();
        List<String> ingredientNames = parseIngredientText(ingredientText);

        for (String name : ingredientNames) {
            Ingredient ingredient = getIngredientByName(name.trim());
            if (ingredient != null) {
                foundIngredients.add(ingredient);
            } else {
                // Buat bahan yang tidak dikenal
                Ingredient unknown = new Ingredient(name.trim(), name.trim(), Constants.RATING_NEUTRAL, "unknown", "Ingredient not found in database", "unknown");
                foundIngredients.add(unknown);
            }
        }
        return new AnalysisResult(foundIngredients);
    }

    private List<String> parseIngredientText(String text) {
        String cleanText = text.replaceAll("\\([^)]*\\)", "");
        String[] parts = cleanText.split("[,;\\n]+");
        List<String> ingredients = new ArrayList<>();
        for (String part : parts) {
            String cleaned = part.trim();
            cleaned = cleaned.replaceAll("^\\d+\\.\\s*", "");
            if (!cleaned.isEmpty()) {
                ingredients.add(cleaned);
            }
        }
        return ingredients;
    }
}
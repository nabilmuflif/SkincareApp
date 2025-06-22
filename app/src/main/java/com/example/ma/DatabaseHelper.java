package com.example.ma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log; // Import Log class

import java.util.ArrayList; //
import java.util.Arrays; //
import java.util.List; //

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper"; // For logging
    private static final String DATABASE_NAME = "incidecoder.db"; //
    private static final int DATABASE_VERSION = 1; //

    // Tables
    private static final String TABLE_PRODUCTS = "products"; //
    private static final String TABLE_INGREDIENTS = "ingredients"; //

    // Product columns
    private static final String PRODUCT_ID = "id"; //
    private static final String PRODUCT_NAME = "name"; //
    private static final String PRODUCT_BRAND = "brand"; //
    private static final String PRODUCT_INGREDIENTS = "ingredients"; //
    private static final String PRODUCT_CATEGORY = "category"; //
    private static final String PRODUCT_CREATED_AT = "created_at"; //

    // Ingredient columns
    private static final String INGREDIENT_ID = "id"; //
    private static final String INGREDIENT_NAME = "name"; //
    private static final String INGREDIENT_COMMON_NAME = "common_name"; //
    private static final String INGREDIENT_RATING = "rating"; //
    private static final String INGREDIENT_WHAT_IT_DOES = "what_it_does"; //
    private static final String INGREDIENT_DESCRIPTION = "description"; //
    private static final String INGREDIENT_IRRITANCY = "irritancy_level"; //
    private static final String INGREDIENT_SOURCES = "sources"; //

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); //
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create products table
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PRODUCT_NAME + " TEXT NOT NULL," +
                PRODUCT_BRAND + " TEXT NOT NULL," + //
                PRODUCT_INGREDIENTS + " TEXT," + //
                PRODUCT_CATEGORY + " TEXT," + //
                PRODUCT_CREATED_AT + " INTEGER" + ")"; //
        // Create ingredients table
        String createIngredientsTable = "CREATE TABLE " + TABLE_INGREDIENTS + "(" +
                INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                INGREDIENT_NAME + " TEXT UNIQUE NOT NULL," +
                INGREDIENT_COMMON_NAME + " TEXT," +
                INGREDIENT_RATING + " TEXT," + //
                INGREDIENT_WHAT_IT_DOES + " TEXT," + //
                INGREDIENT_DESCRIPTION + " TEXT," + //
                INGREDIENT_IRRITANCY + " TEXT," + //
                INGREDIENT_SOURCES + " TEXT" + ")"; //
        db.execSQL(createProductsTable); //
        db.execSQL(createIngredientsTable); //
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS); //
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS); //
        onCreate(db); //
    }

    // Product methods
    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase(); //
        ContentValues values = new ContentValues(); //

        values.put(PRODUCT_NAME, product.getName()); //
        values.put(PRODUCT_BRAND, product.getBrand()); //
        values.put(PRODUCT_INGREDIENTS, product.getIngredients()); //
        values.put(PRODUCT_CATEGORY, product.getCategory()); //
        values.put(PRODUCT_CREATED_AT, System.currentTimeMillis()); //
        long id = db.insert(TABLE_PRODUCTS, null, values); //
        db.close(); //
        return id; //
    }

    public List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>(); //
        SQLiteDatabase db = this.getReadableDatabase(); //

        String selection = PRODUCT_NAME + " LIKE ? OR " + PRODUCT_BRAND + " LIKE ?"; //
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%"}; //
        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection, selectionArgs,
                null, null, PRODUCT_CREATED_AT + " DESC"); //
        if (cursor.moveToFirst()) { //
            do {
                Product product = cursorToProduct(cursor); //
                products.add(product); //
            } while (cursor.moveToNext()); //
        }

        cursor.close(); //
        db.close(); //
        return products; //
    }

    public List<Product> getRecentProducts(int limit) {
        List<Product> products = new ArrayList<>(); //
        SQLiteDatabase db = this.getReadableDatabase(); //

        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null,
                PRODUCT_CREATED_AT + " DESC", String.valueOf(limit)); //
        if (cursor.moveToFirst()) { //
            do {
                Product product = cursorToProduct(cursor); //
                products.add(product); //
            } while (cursor.moveToNext()); //
        }

        cursor.close(); //
        db.close(); //
        return products; //
    }

    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase(); //
        Cursor cursor = db.query(TABLE_PRODUCTS, null, PRODUCT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null); //
        Product product = null; //
        if (cursor.moveToFirst()) { //
            product = cursorToProduct(cursor); //
        }

        cursor.close(); //
        db.close(); //
        return product; //
    }

    public List<Ingredient> searchIngredients(String query) {
        List<Ingredient> ingredients = new ArrayList<>(); //
        SQLiteDatabase db = this.getReadableDatabase(); //

        String selection = INGREDIENT_NAME + " LIKE ? OR " + INGREDIENT_COMMON_NAME + " LIKE ?"; //
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%"}; //
        Cursor cursor = db.query(TABLE_INGREDIENTS, null, selection, selectionArgs,
                null, null, INGREDIENT_NAME + " ASC"); //
        if (cursor.moveToFirst()) { //
            do {
                Ingredient ingredient = cursorToIngredient(cursor); //
                ingredients.add(ingredient); //
            } while (cursor.moveToNext()); //
        }

        cursor.close(); //
        db.close(); //
        return ingredients; //
    }

    public Ingredient getIngredientByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase(); //
        Cursor cursor = db.query(TABLE_INGREDIENTS, null, INGREDIENT_NAME + "=?",
                new String[]{name}, null, null, null); //
        Ingredient ingredient = null; //
        if (cursor.moveToFirst()) { //
            ingredient = cursorToIngredient(cursor); //
        }

        cursor.close(); //
        db.close(); //
        return ingredient; //
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>(); //
        SQLiteDatabase db = this.getReadableDatabase(); //

        Cursor cursor = db.query(TABLE_INGREDIENTS, null, null, null, null, null,
                INGREDIENT_NAME + " ASC"); //
        if (cursor.moveToFirst()) { //
            do {
                Ingredient ingredient = cursorToIngredient(cursor); //
                ingredients.add(ingredient); //
            } while (cursor.moveToNext()); //
        }

        cursor.close(); //
        db.close(); //
        return ingredients; //
    }

    // Tambahkan method untuk validasi input
    public boolean isValidIngredient(Ingredient ingredient) {
        return ingredient != null &&
                !TextUtils.isEmpty(ingredient.getName()) &&
                !TextUtils.isEmpty(ingredient.getRating()); //
    }

    public boolean isValidProduct(Product product) {
        return product != null &&
                !TextUtils.isEmpty(product.getName()) &&
                !TextUtils.isEmpty(product.getBrand()); //
    }

    // Update addIngredient method dengan validation
    public long addIngredient(Ingredient ingredient) {
        if (!isValidIngredient(ingredient)) { //
            Log.e(TAG, "Invalid ingredient data provided.");
            return -1; //
        }

        SQLiteDatabase db = null; //
        try {
            db = this.getWritableDatabase(); //
            ContentValues values = new ContentValues(); //

            values.put(INGREDIENT_NAME, ingredient.getName()); //
            values.put(INGREDIENT_COMMON_NAME, ingredient.getCommonName()); //
            values.put(INGREDIENT_RATING, ingredient.getRating()); //
            values.put(INGREDIENT_WHAT_IT_DOES, ingredient.getWhatItDoes()); //
            values.put(INGREDIENT_DESCRIPTION, ingredient.getDescription()); //
            values.put(INGREDIENT_IRRITANCY, ingredient.getIrritancyLevel()); //
            values.put(INGREDIENT_SOURCES, ingredient.getSources()); //
            return db.insertWithOnConflict(TABLE_INGREDIENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE); //
        } catch (Exception e) {
            Log.e(TAG, "Error adding ingredient: " + e.getMessage(), e); // Log the exception
            return -1; //
        } finally {
            if (db != null) { //
                db.close(); //
            }
        }
    }

    // Helper methods
    private Product cursorToProduct(Cursor cursor) {
        Product product = new Product(); //
        int idIndex = cursor.getColumnIndex(PRODUCT_ID);
        if (idIndex != -1) product.setId(cursor.getInt(idIndex));

        int nameIndex = cursor.getColumnIndex(PRODUCT_NAME);
        if (nameIndex != -1) product.setName(cursor.getString(nameIndex));

        int brandIndex = cursor.getColumnIndex(PRODUCT_BRAND);
        if (brandIndex != -1) product.setBrand(cursor.getString(brandIndex));

        int ingredientsIndex = cursor.getColumnIndex(PRODUCT_INGREDIENTS);
        if (ingredientsIndex != -1) product.setIngredients(cursor.getString(ingredientsIndex));

        int categoryIndex = cursor.getColumnIndex(PRODUCT_CATEGORY);
        if (categoryIndex != -1) product.setCategory(cursor.getString(categoryIndex));

        int createdAtIndex = cursor.getColumnIndex(PRODUCT_CREATED_AT);
        if (createdAtIndex != -1) product.setCreatedAt(cursor.getLong(createdAtIndex));
        return product; //
    }

    private Ingredient cursorToIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient(); //
        int idIndex = cursor.getColumnIndex(INGREDIENT_ID);
        if (idIndex != -1) ingredient.setId(cursor.getInt(idIndex));

        int nameIndex = cursor.getColumnIndex(INGREDIENT_NAME);
        if (nameIndex != -1) ingredient.setName(cursor.getString(nameIndex));

        int commonNameIndex = cursor.getColumnIndex(INGREDIENT_COMMON_NAME);
        if (commonNameIndex != -1) ingredient.setCommonName(cursor.getString(commonNameIndex));

        int ratingIndex = cursor.getColumnIndex(INGREDIENT_RATING);
        if (ratingIndex != -1) ingredient.setRating(cursor.getString(ratingIndex));

        int whatItDoesIndex = cursor.getColumnIndex(INGREDIENT_WHAT_IT_DOES);
        if (whatItDoesIndex != -1) ingredient.setWhatItDoes(cursor.getString(whatItDoesIndex));

        int descriptionIndex = cursor.getColumnIndex(INGREDIENT_DESCRIPTION);
        if (descriptionIndex != -1) ingredient.setDescription(cursor.getString(descriptionIndex));

        int irritancyIndex = cursor.getColumnIndex(INGREDIENT_IRRITANCY);
        if (irritancyIndex != -1) ingredient.setIrritancyLevel(cursor.getString(irritancyIndex));

        int sourcesIndex = cursor.getColumnIndex(INGREDIENT_SOURCES);
        if (sourcesIndex != -1) ingredient.setSources(cursor.getString(sourcesIndex));
        return ingredient; //
    }

    // Initialize default data
    public void initializeDefaultData() {
        if (getAllIngredients().isEmpty()) { //
            addDefaultIngredients(); //
            addDefaultProducts(); //
        }
    }

    private void addDefaultIngredients() {
        // Sample ingredients data
        Ingredient[] defaultIngredients = {
                new Ingredient("Retinyl Palmitate", "Vitamin A Palmitate", Constants.RATING_NO_TAKE, // Menggunakan konstanta
                        "cell-communicating ingredient", //
                        "A form of vitamin A that can be irritating and may cause photosensitivity.", //
                        "medium"), //
                new Ingredient("Azelaic Acid", "Azelaic Acid", Constants.RATING_SUPERSTAR, // Menggunakan konstanta
                        "anti-acne, soothing, buffering", //
                        "A gentle exfoliant that helps with acne and brightens skin.", //
                        "low"), //
                new Ingredient("Ascorbyl Glucoside", "Vitamin C Derivative", Constants.RATING_GOODIE, // Menggunakan konstanta
                        "antioxidant, skin brightening", //
                        "A stable form of Vitamin C that provides antioxidant benefits.", //
                        "low"), //
                new Ingredient("Ceramide EOP", "Ceramide EOP", Constants.RATING_GOODIE, // Menggunakan konstanta
                        "skin-identical ingredient", //
                        "Helps restore and maintain the skin barrier.", //
                        "low"), //
                new Ingredient("Niacinamide", "Vitamin B3", Constants.RATING_SUPERSTAR, // Menggunakan konstanta
                        "anti-acne, skin brightening, anti-aging", //
                        "A versatile ingredient that helps with multiple skin concerns.", //
                        "low") //
        }; //
        for (Ingredient ingredient : defaultIngredients) { //
            addIngredient(ingredient); //
        }
    }

    private void addDefaultProducts() {
        // Sample products data
        Product[] defaultProducts = {
                new Product("Clear Skin Oil Balancing Moisturiser", "Simple",
                        "Aqua, Niacinamide, Glycerin, Azelaic Acid, Ceramide EOP", "Moisturizer"), //
                new Product("Omega + Complex Eye Cream", "Paula's Choice",
                        "Aqua, Retinyl Palmitate, Ascorbyl Glucoside, Ceramide EOP", "Eye Cream"), //
                new Product("Vitamin C Paste", "Lixir",
                        "Ascorbyl Glucoside, Niacinamide, Glycerin", "Serum") //
        }; //
        for (Product product : defaultProducts) { //
            addProduct(product); //
        }
    }

    // Analyze ingredients from text
    public AnalysisResult analyzeIngredients(String ingredientText) {
        List<Ingredient> foundIngredients = new ArrayList<>(); //
        List<String> ingredientNames = parseIngredientText(ingredientText); //

        for (String name : ingredientNames) { //
            Ingredient ingredient = getIngredientByName(name.trim()); //
            if (ingredient != null) { //
                foundIngredients.add(ingredient); //
            } else {
                // Create unknown ingredient
                Ingredient unknown = new Ingredient(name.trim(), name.trim(), Constants.RATING_NEUTRAL, // Menggunakan konstanta
                        "unknown", "Ingredient not found in database", "unknown"); //
                foundIngredients.add(unknown); //
            }
        }

        return new AnalysisResult(foundIngredients); //
    }
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null,
                PRODUCT_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                Product product = cursorToProduct(cursor);
                products.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return products;
    }
    private List<String> parseIngredientText(String text) {
        // Hapus konten dalam kurung (misal: (air), (vitamin E))
        String cleanText = text.replaceAll("\\([^)]*\\)", ""); //
        // Pisahkan berdasarkan koma, titik koma, atau baris baru
        String[] parts = cleanText.split("[,;\\n]+"); //
        List<String> ingredients = new ArrayList<>(); //

        for (String part : parts) { //
            String cleaned = part.trim(); //
            // Hapus angka di awal dan titik (misal: "1. Aqua" menjadi "Aqua")
            cleaned = cleaned.replaceAll("^\\d+\\.\\s*", ""); //
            if (!cleaned.isEmpty()) { // Gunakan !cleaned.isEmpty() daripada cleaned.length() > 1
                ingredients.add(cleaned); //
            }
        }
        return ingredients; //
    }
}
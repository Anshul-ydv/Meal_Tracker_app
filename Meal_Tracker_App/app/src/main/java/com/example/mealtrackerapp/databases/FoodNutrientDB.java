package com.example.mealtrackerapp.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Class meant for copying an SQLite database file from assets folder.
 * Extends SQLiteAssetHelper, a library that allows to perform the copy from the assets folder more easily.
 */
public class FoodNutrientDB extends SQLiteAssetHelper {

    /**
     * The constructor simply copies the database named foodnutrients.db from the assets folder
     * @param context
     */
    public FoodNutrientDB(Context context) {
        super(context, "foodnutrients.db", null, 1);
    }

    /**
     * Returns a list of all the data in the database stored as FoodNutrients objects.
     * @return ArrayList of FoodNutrient objects
     */
    public List<FoodNutrients> getFoodNutrients() {
        List<FoodNutrients> foodNutrientsList = new ArrayList<>();

        //get all data from db ordered alphabetically
        String query = "SELECT * FROM " + "foodnutrients" + " ORDER BY FOODNAME ASC";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        //each row is stored as a FoodNutrients object and added to an ArrayList, which is finally returned
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int calories = cursor.getInt(2);
                double carbs = cursor.getDouble(3);
                double protein = cursor.getDouble(4);
                double fat = cursor.getDouble(5);

                FoodNutrients foodNutrients = new FoodNutrients(id, name, calories, carbs, protein, fat);
                foodNutrientsList.add(foodNutrients);

            } while (cursor.moveToNext());
        } else {

        }
        cursor.close();
        db.close();

        return foodNutrientsList;
    }

    /**
     * Get a FoodNutrients object containing all nutritional info of a food. Search by name
     * @param name name of the food of which we can to get nutrition values
     * @return FoodNutrients object containing nutrition value
     */
    public FoodNutrients getFoodNutrientsByName(String name) {
        FoodNutrients foodNutrients;

        String query = "SELECT * FROM " + "foodnutrients WHERE FOODNAME LIKE \'" + name + "\'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String foodname = cursor.getString(1);
            int calories = cursor.getInt(2);
            double carbs = cursor.getDouble(3);
            double protein = cursor.getDouble(4);
            double fat = cursor.getDouble(5);

            foodNutrients = new FoodNutrients(id, foodname, calories, carbs, protein, fat);
        } else {
            foodNutrients = new FoodNutrients();
        }

        return foodNutrients;
    }
}

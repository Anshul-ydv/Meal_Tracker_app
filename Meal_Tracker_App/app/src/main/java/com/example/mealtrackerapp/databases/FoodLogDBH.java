package com.example.mealtrackerapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite database helper. It handles all methods related to the foodLogs.db
 * The first time the database helper is called, the foodLogs.db is created.
 * The daydata.db contains all the information about a user's food diary for every day the app is used.
 */
public class  FoodLogDBH extends SQLiteOpenHelper {
    //constants for table column names
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_FOOD = "FOOD";
    public static final String FOODLOGS_TABLE = "FOODLOGS_TABLE";
    public static final String COLUMN_MEAL = "MEAL";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TIME = "TIME";
    public static final String COLUMN_CALORIES = "CALORIES";
    public static final String COLUMN_CARBS = "CARBS";
    public static final String COLUMN_PROTEIN = "PROTEIN";
    public static final String COLUMN_FAT = "FAT";

    /**
     * On first construction, creates a database with a name and a version
     * @param context activity class
     */
    public FoodLogDBH(@Nullable Context context) {
        super(context, "foodLogs.db", null, 1);
    }

    /**
     * Used only the first time. On first construction, creates a database daydata.db with one table DAYDATA_TABLE with columns for
     * int id (primary key and autoincrementing), String date, String name, String meal, String time, int calories, double carbs, double protein, double fat
     * @param db database created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableString = "CREATE TABLE " + FOODLOGS_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " + COLUMN_FOOD + " TEXT, " + COLUMN_MEAL + " TEXT, " +
                COLUMN_TIME + " TEXT, " + COLUMN_CALORIES + " INTEGER, " + COLUMN_CARBS + " REAL, " +
                COLUMN_PROTEIN + " REAL, " + COLUMN_FAT + " REAL)";

        db.execSQL(createTableString);
    }

    /**
     * Handles actions on upgrade to another version.
     * @param db database upgraded
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Updates the values of an entry for a given food log. Date stays the same.
     * @param foodLog food log object to be altered
     * @param name name of the food log
     * @param meal meal of the food log
     * @param time time of the food log
     * @param calories caloric value of the food log
     * @param carbs carb value of the food log
     * @param protein protein value of the food log
     * @param fat fat value of the food log
     */
    public void updateOne(FoodLog foodLog, String name, String meal, String time, int calories, double carbs, double protein, double fat) {
        //get the database in writeable mode
        SQLiteDatabase db = this.getWritableDatabase();
        //creating content values for modifications
        ContentValues cv = new ContentValues();
        //adding pairs of values (column name and data input) to content values for upcoming update
        cv.put(COLUMN_FOOD, name);
        cv.put(COLUMN_MEAL, meal);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_CALORIES, calories);
        cv.put(COLUMN_CARBS, carbs);
        cv.put(COLUMN_PROTEIN, protein);
        cv.put(COLUMN_FAT, fat);
        //updating table with content value where the id column of the log corresponds to the id of the food log given in parameters
        db.update(FOODLOGS_TABLE, cv, COLUMN_ID + " = " + foodLog.getId(), null);
    }

    /**
     * Delete a row in the table where the id equals to the id of the food log given as a parameter
     * @param foodlog to be deleted
     */
    public void deleteOne(FoodLog foodlog) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(FOODLOGS_TABLE, COLUMN_ID + " = " + foodlog.getId(), null);
    }

    /**
     * Inserting an ArrayList of food log objects given in parameters into the db
     * @param foodlogList ArrayList to be inserted
     */
    public void addArray(ArrayList<FoodLog> foodlogList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //for each food logs in the list, insert
        for (FoodLog foodLog: foodlogList) {
            cv.put(COLUMN_DATE, foodLog.getDate());
            cv.put(COLUMN_FOOD, foodLog.getName());
            cv.put(COLUMN_MEAL, foodLog.getMeal());
            cv.put(COLUMN_TIME, foodLog.getTime());
            cv.put(COLUMN_CALORIES, foodLog.getCalories());
            cv.put(COLUMN_CARBS, foodLog.getCarbs());
            cv.put(COLUMN_PROTEIN, foodLog.getProtein());
            cv.put(COLUMN_FAT, foodLog.getFat());
            db.insert(FOODLOGS_TABLE, null, cv);
        }
    }

    /**
     * Get an ArrayList of all the food logs for a given date. They are ordered by time (latest log first).
     * A query is made for all rows where the date corresponds to the one given in parameter.
     * Each of these rows is then converted into a FoodLog object and added to an ArrayList, which is then returned.
     * @param dateDisplayed date for the food logs to display
     * @return ArrayList of FoodLog objects
     */
    public List<FoodLog> getFoodLog(String dateDisplayed) {
        List<FoodLog> foodLogList = new ArrayList<>();

        //get data from db where date column = date requested
        String query = "SELECT * FROM " + FOODLOGS_TABLE  + " WHERE " + COLUMN_DATE + " LIKE \'" + dateDisplayed + "\' ORDER BY " + COLUMN_TIME + " DESC";

        //get db in read-only
        SQLiteDatabase db = this.getReadableDatabase();

        //getting the results of the query
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            //loop through cursor (result set), while it is possible to move to the next row, and create new foodLog object. then add them to the return array
            do {
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                String food = cursor.getString(2);
                String meal = cursor.getString(3);
                String time = cursor.getString(4);
                int calories = cursor.getInt(5);
                double carbs = cursor.getInt(6);
                double protein = cursor.getInt(7);
                double fat = cursor.getInt(8);

                FoodLog foodLog = new FoodLog(id, date, food, meal, time, calories, carbs, protein, fat);
                foodLogList.add(foodLog);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return foodLogList;
    }

    /**
     * Get the sum of all calories eaten for a given date.
     * @param date date for which calories will be summed
     * @return Integer sum of all calories for the given date
     */
    public int getCaloriesByDate(String date) {
        int sumOfCalories;

        //query to get the sum of calories on a given date
        String query = "SELECT SUM(" + COLUMN_CALORIES + ") FROM " + FOODLOGS_TABLE + " WHERE " + COLUMN_DATE + " LIKE \'" + date + "\'";
        //get read-only database
        SQLiteDatabase db = this.getReadableDatabase();
        //query results
        Cursor cursor = db.rawQuery(query, null);
        //if there is entries for the given date, store the sum of calories in a variable. if not, return 0
        if (cursor.moveToFirst()) {
            sumOfCalories = cursor.getInt(0);
        } else {
            sumOfCalories = 0;
        }
        cursor.close();

        return sumOfCalories;
    }

    /**
     * Get the sum of all calories eaten on a specific meal on a given date.
     * @param meal meal for which calories will be summed
     * @param date date for which calories will be summed
     * @return Integer sum of calories for a meal on the given date
     */
    public int getCaloriesByMealByDate(String meal, String date) {
        int sumOfCalories;

        //query to get the sum of calories on a given date for a specific meal
        String query = "SELECT SUM(" + COLUMN_CALORIES + ") FROM " + FOODLOGS_TABLE + " WHERE " + COLUMN_MEAL + " LIKE \'" + meal + "\' AND " + COLUMN_DATE + " LIKE \'" + date + "\'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        //if there is entries for the given date, store the sum of calories in a variable. if not, return 0
        if (cursor.moveToFirst()) {
            sumOfCalories = cursor.getInt(0);
        } else {
            sumOfCalories = 0;
        }
        cursor.close();

        return sumOfCalories;
    }

    /**
     * Get the sum of any column for a given date
     * @param column column for which calories will be summed
     * @param date date for which calories will be summed
     * @return Integer sum of the specified column on a given date
     */
    public int getSumByDate(String column, String date) {
        int returnSum;

        String query = "SELECT SUM(" + column + ") FROM " + FOODLOGS_TABLE + " WHERE " + COLUMN_DATE + " LIKE \'" + date + "\'" ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            returnSum = cursor.getInt(0);
        } else {
            returnSum = 0;
        }
        cursor.close();
        db.close();

        return returnSum;
    }
}

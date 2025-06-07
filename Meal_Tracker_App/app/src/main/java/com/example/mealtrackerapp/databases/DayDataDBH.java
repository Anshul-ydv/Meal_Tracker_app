package com.example.mealtrackerapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * SQLite database helper. It handles all methods related to the daydata.db.
 * The first time the database helper is called, the daydata.db is created.
 * The daydata.db contains all the information about a user's set goals for every day the app is used.
 * In addition it contains the water count for each day.
 */
public class DayDataDBH extends SQLiteOpenHelper {
    //column name constants
    public static final String COLUMN_ID = "ID";
    public static final String DAYDATA_TABLE = "DAYDATA_TABLE";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_WEIGHT = "WEIGHT";
    public static final String COLUMN_CALORIC_GOAL = "CALORIC_GOAL";
    public static final String COLUMN_CARBS_GOAL = "CARBS_GOAL";
    public static final String COLUMN_PROTEIN_GOAL = "PROTEIN_GOAL";
    public static final String COLUMN_FAT_GOAL = "FAT_GOAL";
    public static final String COLUMN_WATER_GOAL = "WATER_GOAL";
    public static final String COLUMN_WATER_INTAKE = "WATER_INTAKE";

    /**
     * On first construction, creates a database with a name and a version
     * @param context activity class
     */
    public DayDataDBH(@Nullable Context context) {
        super(context, "daydata.db", null, 1);
    }

    /**
     * Used only the first time. On first construction, creates a database daydata.db with one table DAYDATA_TABLE with columns for
     * int id (primary key and autoincrementing), String date, int weight, int caloricGoal, int carbsGoal, int proteinGoal, int fatGoal, int waterGoal, int waterIntake
     * The date column has to be unique (only one row per date)
     * @param db database created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableString = "CREATE TABLE " + DAYDATA_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATE + " TEXT UNIQUE NOT NULL, " +
                COLUMN_WEIGHT + " INTEGER, " + COLUMN_CALORIC_GOAL + " INTEGER, " + COLUMN_CARBS_GOAL + " INTEGER, " +
                COLUMN_PROTEIN_GOAL + " INTEGER, " + COLUMN_FAT_GOAL + " INTEGER, " + COLUMN_WATER_GOAL + " INTEGER, " +
                COLUMN_WATER_INTAKE + " INTEGER)";

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
     * Adds a new row or replaces an existing one.
     * @param date date of data
     * @param weight weight input by user for that date
     * @param caloricGoal calorie intake goal input by user for that date
     * @param carbsGoal carb intake goal input by user for that date
     * @param proteinGoal protein intake goal input by user for that date
     * @param fatGoal fat intake goal input by user for that date
     * @param waterGoal water intake goal input by user for that date
     * @param waterIntake water intake input by user for that day
     */
    public void addOneReplace(String date, int weight, int caloricGoal, int carbsGoal, int proteinGoal, int fatGoal, int waterGoal, int waterIntake) {
        //get the database in writeable mode
        SQLiteDatabase db = this.getWritableDatabase();
        //creating content values to add a new row in the database
        ContentValues cv = new ContentValues();

        //adding pairs of values (column name and data input) to content values for future insert to table
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_WEIGHT, weight);
        cv.put(COLUMN_CALORIC_GOAL, caloricGoal);
        cv.put(COLUMN_CARBS_GOAL, carbsGoal);
        cv.put(COLUMN_PROTEIN_GOAL, proteinGoal);
        cv.put(COLUMN_FAT_GOAL, fatGoal);
        cv.put(COLUMN_WATER_GOAL, waterGoal);
        cv.put(COLUMN_WATER_INTAKE, waterIntake);

        //inserting content values to table. if there is data for that date, it is replaced
        db.insertWithOnConflict(DAYDATA_TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Adds a new row if no data exists for the given date.
     * @param date date of data
     * @param weight weight input by user for that date
     * @param caloricGoal calorie intake goal input by user for that date
     * @param carbsGoal carb intake goal input by user for that date
     * @param proteinGoal protein intake goal input by user for that date
     * @param fatGoal fat intake goal input by user for that date
     * @param waterGoal water intake goal input by user for that date
     * @param waterIntake water intake input by user for that day
     */
    public void addOneIgnore(String date, int weight, int caloricGoal, int carbsGoal, int proteinGoal, int fatGoal, int waterGoal, int waterIntake) {
        //get the database in writeable mode
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_WEIGHT, weight);
        cv.put(COLUMN_CALORIC_GOAL, caloricGoal);
        cv.put(COLUMN_CARBS_GOAL, carbsGoal);
        cv.put(COLUMN_PROTEIN_GOAL, proteinGoal);
        cv.put(COLUMN_FAT_GOAL, fatGoal);
        cv.put(COLUMN_WATER_GOAL, waterGoal);
        cv.put(COLUMN_WATER_INTAKE, waterIntake);

        //inserting content values to table. if there is data for that date, nothing happens
        db.insertWithOnConflict(DAYDATA_TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Getting data from a column in the database given a certain date
     * @param column column name of the data to be fetched
     * @param date date at which the data should be fetched
     * @return Integer of the value requested
     */
    public int getIntByDate(String column, String date) {
        int returnInt;

        //SQL query to get data from a given column and where the date is as determined
        String query = "SELECT " + column + " FROM " + DAYDATA_TABLE + " WHERE " + COLUMN_DATE + " LIKE \'" + date + "\'";

        //get the database but in read-only mode
        SQLiteDatabase db = this.getReadableDatabase();

        //result of the query
        Cursor cursor = db.rawQuery(query, null);

        //if the cursor is able to move to the first row (data exists) them the requested int is returned. if not, 0 is returned
        if (cursor.moveToFirst()) {
            returnInt = cursor.getInt(0);
        } else {
            returnInt = 0;
        }
        cursor.close();

        return returnInt;
    }

    /**
     * Update a column with new given values on a given date
     * @param column column to be updated
     * @param date date where the modification should happen
     * @param newValue new value for the column
     */
    public void updateColumnWhereDateTo(String column, String date, int newValue) {
        //SQL query to update a column to a new value where the date is as specified
        String query = "UPDATE " + DAYDATA_TABLE + " SET " + column + " = " + newValue + " WHERE " + COLUMN_DATE + " LIKE \'" + date + "\'";
        //get the database in writeable mode
        SQLiteDatabase db = this.getWritableDatabase();
        //execute the query
        db.execSQL(query);
    }
}

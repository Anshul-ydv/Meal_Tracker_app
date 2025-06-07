package com.example.mealtrackerapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealtrackerapp.databases.DayDataDBH;
import com.example.mealtrackerapp.R;

import java.util.Calendar;

import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_CALORIC_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_CARBS_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_FAT_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_PROTEIN_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_WATER_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_WATER_INTAKE;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_WEIGHT;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_DATE;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_DAY;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_MONTH;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_YEAR;

/**
 * Profile and goal setup activity. The user inputs basic info and personal goals used in the app. Can be used to update info as well.
 */
public class SetupActivity extends AppCompatActivity implements View.OnClickListener {
    //constants used for shared preferences, avoiding typing raw string every time.
    public static final String PREF_CALORIC_GOAL = "pref_caloricGoal";
    public static final String PREF_CARBS = "pref_carbs";
    public static final String PREF_PROTEIN = "pref_protein";
    public static final String PREF_FAT = "pref_fat";
    public static final String PREF_WATER_GOAL = "pref_watergoal";
    public static final String SETUP_PREF = "sharedpref_setupPref";
    public static final String PREF_FIRSTNAME = "pref_firstname";
    public static final String PREF_LASTNAME ="pref_lastname";
    public static final String PREF_BIRTHDATE = "pref_birthdate";
    public static final String PREF_HEIGHT ="pref_height";
    public static final String PREF_WEIGHT = "pref_weight";

    //initiating variables so that they're global
    EditText etxtSetFirstname, etxtSetLastname, etxtSetHeight, etxtSetWeightValue, etxtSetCaloricValue,
            etxtSetCarbs, etxtSetProtein, etxtSetFat, etxtSetWater;
    Button btnSave;
    ImageView btnBirthdateCalendar;
    TextView txtSetBirthdate;
    String firstname, lastname, birthdate = " ", today, dateDisplayed;
    int height, weight, caloricGoal, carbsGoal, proteinGoal, fatGoal, waterGoal, waterIntake, yearToday, monthToday, dayToday, year, month, day;
    Calendar c;
    TextView txtDate;
    DayDataDBH dayDataDHB;
    SharedPreferences setupPref;
    SharedPreferences.Editor prefEditor;
    Intent intentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //get current date and display it by default (first launch)
        c = Calendar.getInstance();
        yearToday = c.get(Calendar.YEAR);
        monthToday = c.get(Calendar.MONTH);
        dayToday = c.get(Calendar.DAY_OF_MONTH);

        day = dayToday;
        month = monthToday;
        year = yearToday;

        today = dayToday + "-" + (monthToday + 1) + "-" + yearToday;
        dateDisplayed = today;

        //reference to layout elements
        btnBirthdateCalendar = findViewById(R.id.btnBirthdateCalendar);
        btnBirthdateCalendar.setOnClickListener(this);
        etxtSetFirstname = findViewById(R.id.etxtSetFirstname);
        etxtSetLastname = findViewById(R.id.etxtSetLastname);
        etxtSetHeight = findViewById(R.id.etxtSetHeight);
        etxtSetWeightValue = findViewById(R.id.etxtSetWeightValue);
        etxtSetCaloricValue = findViewById(R.id.etxtSetCaloricValue);
        etxtSetCarbs = findViewById(R.id.etxtSetCarbs);
        etxtSetProtein = findViewById(R.id.etxtSetProtein);
        etxtSetFat = findViewById(R.id.etxtSetFat);
        etxtSetWater= findViewById(R.id.etxtSetWater);
        txtSetBirthdate = findViewById(R.id.txtBirthdate);

        //get shared preferences and editor
        setupPref = getSharedPreferences(SETUP_PREF, Activity.MODE_PRIVATE);
        prefEditor = setupPref.edit();

        //implementing save button function
        btnSave = findViewById(R.id.btnSaveSetup);
        btnSave.setOnClickListener(new View.OnClickListener() {
            /**
             * When the save button is clicked, several conditions are tested on the user's input (e.g. empty).
             * If the conditions are met, the inputs are saved in shared preferences for future use.
             * The database containing the goals of the user for each day is also updated for the given date (date chosen in main activity).
             * @param v button save
             */
            @Override
            public void onClick(View v) {
                //making sure that none of the edit text views are empty. some of the fields cannot be 0
                if (editTextIsEmpty(etxtSetFirstname)) {
                    etxtSetFirstname.setError("Please enter first name");
                    return;
                } if (editTextIsEmpty(etxtSetLastname)) {
                    etxtSetLastname.setError("Please enter last name");
                    return;
                } if (birthdate.equals(" ")) {
                    Toast.makeText(SetupActivity.this, "Please enter birth date", Toast.LENGTH_SHORT).show();
                    txtSetBirthdate.setTextColor(ContextCompat.getColor(SetupActivity.this, R.color.red));
                    return;
                } if (editTextIsEmpty(etxtSetHeight) || editTextIsZero(etxtSetHeight)) {
                    etxtSetHeight.setError("Please enter height");
                    return;
                } if (editTextIsEmpty(etxtSetWeightValue) || editTextIsZero(etxtSetWeightValue)) {
                    etxtSetWeightValue.setError("Please enter weight");
                    return;
                } if (editTextIsEmpty(etxtSetCaloricValue) || editTextIsZero(etxtSetCaloricValue)) {
                    etxtSetCaloricValue.setError("Please enter daily caloric goal");
                    return;
                } if (editTextIsEmpty(etxtSetCarbs)) {
                    etxtSetCarbs.setError("Please enter carbs amount");
                    return;
                } if (editTextIsEmpty(etxtSetProtein)) {
                    etxtSetProtein.setError("Please enter protein amount");
                    return;
                } if (editTextIsEmpty(etxtSetFat)) {
                    etxtSetFat.setError("Please enter fat amount");
                    return;
                } if (editTextIsEmpty(etxtSetWater)) {
                    etxtSetWater.setError("Please enter number of water glasses");
                    return;
                    //if the macronutrient fields are not empty, reminding the user that their sum should be 100 since they are percents
                } else if (!editTextIsEmpty(etxtSetCarbs) &&
                        !editTextIsEmpty(etxtSetCarbs) &&
                        !editTextIsEmpty(etxtSetCarbs)) {
                    int carbs = Integer.parseInt(etxtSetCarbs.getText().toString());
                    int protein = Integer.parseInt(etxtSetProtein.getText().toString());
                    int fat = Integer.parseInt(etxtSetFat.getText().toString());
                    if (carbs + protein + fat != 100) {
                        Toast.makeText(SetupActivity.this, "Sum of macro should be 100", Toast.LENGTH_SHORT).show();
                    } else {
                        //gathering the inputs
                        weight = editTextToInt(etxtSetWeightValue);
                        caloricGoal = editTextToInt(etxtSetCaloricValue);
                        carbsGoal = editTextToInt(etxtSetCarbs);
                        proteinGoal = editTextToInt(etxtSetProtein);
                        fatGoal = editTextToInt(etxtSetFat);
                        waterGoal = editTextToInt(etxtSetWater);
                        //putting them to shared preferences (those settings will be used for all future dates if not changed)
                        prefEditor.putString(PREF_FIRSTNAME, etxtSetFirstname.getText().toString());
                        prefEditor.putString(PREF_LASTNAME, etxtSetLastname.getText().toString());
                        prefEditor.putString(PREF_BIRTHDATE, birthdate);
                        prefEditor.putInt(PREF_HEIGHT, editTextToInt(etxtSetHeight));
                        prefEditor.putInt(PREF_WEIGHT, weight);
                        prefEditor.putInt(PREF_CALORIC_GOAL, caloricGoal);
                        prefEditor.putInt(PREF_CARBS, carbsGoal);
                        prefEditor.putInt(PREF_PROTEIN, proteinGoal);
                        prefEditor.putInt(PREF_FAT, fatGoal);
                        prefEditor.putInt(PREF_WATER_GOAL, waterGoal);
                        prefEditor.apply();

                        //sending back the date selected by the user in the main activity so that the UI keeps the same date
                        intentMain.putExtra(EXTRA_DISPLAYED_DATE, dateDisplayed);
                        intentMain.putExtra(EXTRA_DISPLAYED_DAY, day);
                        intentMain.putExtra(EXTRA_DISPLAYED_MONTH, month);
                        intentMain.putExtra(EXTRA_DISPLAYED_YEAR, year);

                        //updating database containing settings for each day the app is used for
                        dayDataDHB.addOneReplace(dateDisplayed, weight, caloricGoal, carbsGoal, proteinGoal, fatGoal, waterGoal, waterIntake);
                        //starting main activity
                        startActivity(intentMain);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //call daydata.db helper to gather previously entered data for the given date or setting new data
        dayDataDHB = new DayDataDBH(SetupActivity.this);

        //get date displayed on main activity (date of today if it is the first launch of the app) and display it
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_DISPLAYED_DATE)) {
            dateDisplayed = intent.getStringExtra(EXTRA_DISPLAYED_DATE);
            day = intent.getIntExtra(EXTRA_DISPLAYED_DAY, dayToday);
            month = intent.getIntExtra(EXTRA_DISPLAYED_MONTH, monthToday);
            year = intent.getIntExtra(EXTRA_DISPLAYED_YEAR, yearToday);
        }
        txtDate = findViewById(R.id.txtDateForSetup);
        txtDate.setText(dateDisplayed);

        //retrieve shared preferences and fill in the fields if they already exist
        if (setupPref.contains(PREF_FIRSTNAME)) {
            firstname = setupPref.getString(PREF_FIRSTNAME, "");
            lastname = setupPref.getString(PREF_LASTNAME, "");
            birthdate = setupPref.getString(PREF_BIRTHDATE, "");
            height = setupPref.getInt(PREF_HEIGHT, 0);

            etxtSetFirstname.setText(firstname);
            etxtSetLastname.setText(lastname);
            txtSetBirthdate.setText(birthdate);
            etxtSetHeight.setText(String.valueOf(height));
        }
        //gathering previously input goals if they exist (if not, default value is 0)
        weight = dayDataDHB.getIntByDate(COLUMN_WEIGHT, dateDisplayed);
        caloricGoal = dayDataDHB.getIntByDate(COLUMN_CALORIC_GOAL, dateDisplayed);
        carbsGoal = dayDataDHB.getIntByDate(COLUMN_CARBS_GOAL, dateDisplayed);
        proteinGoal = dayDataDHB.getIntByDate(COLUMN_PROTEIN_GOAL, dateDisplayed);
        fatGoal = dayDataDHB.getIntByDate(COLUMN_FAT_GOAL, dateDisplayed);
        waterGoal = dayDataDHB.getIntByDate(COLUMN_WATER_GOAL, dateDisplayed);
        waterIntake = dayDataDHB.getIntByDate(COLUMN_WATER_INTAKE, dateDisplayed);
        //set the values to the corresponding fields only if values already exist (if they do not, 0 would be displayed, hiding hints)
        if (caloricGoal != 0) {
            etxtSetWeightValue.setText(String.valueOf(weight));
            etxtSetCaloricValue.setText(String.valueOf(caloricGoal));
            etxtSetCarbs.setText(String.valueOf(carbsGoal));
            etxtSetProtein.setText(String.valueOf(proteinGoal));
            etxtSetFat.setText(String.valueOf(fatGoal));
            etxtSetWater.setText(String.valueOf(waterGoal));
        }

        //prepare intent to main activity
        intentMain = new Intent(SetupActivity.this, MainActivity.class);
        intentMain.putExtra(EXTRA_DISPLAYED_DATE, dateDisplayed);
        intentMain.putExtra(EXTRA_DISPLAYED_DAY, day);
        intentMain.putExtra(EXTRA_DISPLAYED_MONTH, month);
        intentMain.putExtra(EXTRA_DISPLAYED_YEAR, year);
    }

    /**
     * On up button press (home), making sure that the date is sent back to the main
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(intentMain);
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    /**
     * When clicked, the calendar button opens a date picker dialog for birth date
     * @param v calendar ImageView
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBirthdateCalendar) {
            showDateDialog();
        }
    }

    /**
     * Displays the date picker dialog, with the date of today as default values.
     * When the user selects a date, it is displayed in a text view and set as default value in case the date picker is used again.
     */
    private void showDateDialog() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                (view, year, monthOfYear, dayOfMonth) -> {
                    birthdate = dayOfMonth + "-" + (monthOfYear+1) + "-" + year;
                    txtSetBirthdate.setText(birthdate);
                    txtSetBirthdate.setTextColor(ContextCompat.getColor(SetupActivity.this, R.color.black));
                    yearToday = year;
                    monthToday = monthOfYear;
                    dayToday = dayOfMonth;
                }, yearToday, monthToday, dayToday);
        datePickerDialog.show();
    }

    /**
     * Verifies if an EditTextView is empty.
     * @param editText edit text to be checked
     * @return true if empty, false if filled
     */
    public boolean editTextIsEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    /**
     * Verifies if an EditTextView as 0 as input.
     * @param editText edit text to be checked
     * @return true if 0
     */
    public boolean editTextIsZero(EditText editText) {
        return editText.getText().toString().trim().equals("0");
    }

    /**
     * Returns the integer input of an EditTextView
     * @param editText edit text to be extracted from
     * @return integer from EditTextView
     */
    private int editTextToInt (EditText editText) {
        return Integer.parseInt(editText.getText().toString().trim());
    }
}



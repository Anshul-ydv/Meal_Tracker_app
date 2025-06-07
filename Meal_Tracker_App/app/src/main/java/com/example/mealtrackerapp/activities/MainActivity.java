package com.example.mealtrackerapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mealtrackerapp.other.Counter;
import com.example.mealtrackerapp.databases.DayDataDBH;
import com.example.mealtrackerapp.databases.FoodLog;
import com.example.mealtrackerapp.databases.FoodLogDBH;
import com.example.mealtrackerapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

//import all needed constants from other classes
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_CALORIC_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_CARBS_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_FAT_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_PROTEIN_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_WATER_GOAL;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_WATER_INTAKE;
import static com.example.mealtrackerapp.activities.FoodEntryActivity.EXTRA_FOOD_LOG;
import static com.example.mealtrackerapp.databases.FoodLogDBH.COLUMN_CARBS;
import static com.example.mealtrackerapp.databases.FoodLogDBH.COLUMN_FAT;
import static com.example.mealtrackerapp.databases.FoodLogDBH.COLUMN_PROTEIN;
import static com.example.mealtrackerapp.activities.SetupActivity.PREF_CALORIC_GOAL;
import static com.example.mealtrackerapp.activities.SetupActivity.PREF_CARBS;
import static com.example.mealtrackerapp.activities.SetupActivity.PREF_FAT;
import static com.example.mealtrackerapp.activities.SetupActivity.PREF_PROTEIN;
import static com.example.mealtrackerapp.activities.SetupActivity.PREF_WATER_GOAL;
import static com.example.mealtrackerapp.activities.SetupActivity.PREF_WEIGHT;
import static com.example.mealtrackerapp.activities.SetupActivity.SETUP_PREF;

/**
 * Daily status activity. Displays daily progression in caloric intake, macronutrient intake and water intake, and daily food logs.
 * Possibility to switch to another date.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //defining constants for first app launch criteria and for intent extras
    public static final String FIRST_TIME_PREF = "first_time_pref";
    public static final String IS_FIRST_LAUNCH_PREF = "isFirstLaunch";
    public static final String EXTRA_DISPLAYED_DATE = "extra_displayed_date";
    public static final String EXTRA_DISPLAYED_DAY = "extra_displayed_day";
    public static final String EXTRA_DISPLAYED_MONTH = "extra_displayed_month";
    public static final String EXTRA_DISPLAYED_YEAR = "extra_displayed_year";
    //initiating variables as final
    TextView caloricGoalDisplay, eatenDisplay, caloriesLeftDisplay, breakfastDisplay, lunchDisplay,
            dinnerDisplay, extrasDisplay, waterDisplay, waterMinus, waterPlus, dateNowDisplay,
            txtCaloriesLeft, txtCarbs, txtProtein, txtFat, txtEatenDisplay;
    ProgressBar circularPB, carbsPB, proteinPB, fatPB;
    FloatingActionButton fabAdd, fabDatePicker;
    int weightInt, caloricGoalInt, caloriesLeftInt, carbGoalInt, proteinGoalInt, fatGoalInt, waterGoalInt, carbShare,
            proteinShare, fatShare, caloricIntake, carbsIntake, proteinIntake, fatIntake, breakfastIntake,
            lunchIntake, dinnerIntake, extrasIntake, waterIntake;
    ListView lvFoodLogs;

    //counter for water
    Counter waterCounter;

    //shared preferences for profile and goals
    SharedPreferences setupPref;

    //date picker variables
    Calendar calendar;
    int dayToday, monthToday, yearToday, dayDisplayed, monthDisplayed, yearDisplayed;
    String dateToday, dateDisplayed, datePicked;

    //database helpers for goal data and food logs
    DayDataDBH dayDataDHB;
    FoodLogDBH foodLogDBH;


     //Creation of databases. References to needed views. Creation of water counter. Implementation of button functions and OnClickListener for list view.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //handle first launch. the first time, a shared preference value is set to true by default and starts the welcome activity.
        //once this activity is started again, the value is set to false and the welcome activity will never be shown again.
        Boolean isFirstLaunch = getSharedPreferences(FIRST_TIME_PREF, MODE_PRIVATE).getBoolean(IS_FIRST_LAUNCH_PREF, true);
        if (isFirstLaunch) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
        getSharedPreferences(FIRST_TIME_PREF, MODE_PRIVATE).edit().putBoolean(IS_FIRST_LAUNCH_PREF, false).apply();

        //create databases via database helpers for day data and food logs
        dayDataDHB = new DayDataDBH(MainActivity.this);
        foodLogDBH = new FoodLogDBH(MainActivity.this);

        //references to caloric counters view, date view and water count
        caloricGoalDisplay = findViewById(R.id.txtCaloricGoalValue);
        eatenDisplay = findViewById(R.id.txtCaloriesEatenValue);
        caloriesLeftDisplay = findViewById(R.id.txtCaloriesLeftValue);
        breakfastDisplay = findViewById(R.id.txtBreakfastValue);
        lunchDisplay = findViewById(R.id.txtLunchValue);
        dinnerDisplay = findViewById(R.id.txtDinnerValue);
        extrasDisplay = findViewById(R.id.txtExtrasValue);
        dateNowDisplay = findViewById(R.id.txtDateNow);
        waterDisplay = findViewById(R.id.txtWaterCountValue);
        txtCaloriesLeft = findViewById(R.id.txtCaloriesLeft);
        //reference to TextViews for modification upon goal completion
        txtCarbs = findViewById(R.id.txtCarbs);
        txtProtein = findViewById(R.id.txtProteins);
        txtFat = findViewById(R.id.txtFat);
        txtEatenDisplay = findViewById(R.id.txtCaloriesEaten);

        //reference to food log list view
        lvFoodLogs = findViewById(R.id.lvFoodLogs);

        //reference to progress bars
        circularPB = findViewById(R.id.circularProgressionBar);
        carbsPB = findViewById(R.id.carbPB);
        proteinPB = findViewById(R.id.proteinPB);
        fatPB = findViewById(R.id.fatPB);

        //create waterCounter
        waterCounter = new Counter();

        //water counter buttons and functions
        waterMinus = findViewById(R.id.txtWaterMinus);
        waterPlus = findViewById(R.id.txtWaterPlus);

        //when plus or minus is clicked, the water counter is updated on the screen and in the day data database
        waterMinus.setOnClickListener(v -> {
            waterCounter.remove(1);
            dayDataDHB.updateColumnWhereDateTo(COLUMN_WATER_INTAKE, dateDisplayed, waterCounter.getCount());
            updateWaterDisplay(dateDisplayed);
        });
        waterPlus.setOnClickListener(v -> {
            waterCounter.add(1);
            dayDataDHB.updateColumnWhereDateTo(COLUMN_WATER_INTAKE, dateDisplayed, waterCounter.getCount());
            updateWaterDisplay(dateDisplayed);
        });

        //floating action button add to food entry
        fabAdd = findViewById(R.id.fabBtnAdd);

        //when clicked, the food entry activity is started. the date currently displayed is sent as extra for food log
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FoodEntryActivity.class);
            intent.putExtra(EXTRA_DISPLAYED_DATE, dateDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_DAY, dayDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_MONTH, monthDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_YEAR, yearDisplayed);
            startActivity(intent);
        });

        fabDatePicker = findViewById(R.id.fabBtnDate);
        fabDatePicker.setOnClickListener(v -> {
            dayDataDHB.addOneReplace(dateDisplayed, weightInt, caloricGoalInt, carbGoalInt, proteinGoalInt, fatGoalInt, waterGoalInt, waterIntake);
            showDateDialog();
        });
        //if an element of the food log list view is clicked, food entry activity is started. the clicked food log is put in extra, as well as the current displayed date
        /**
         * The food log at the selected position in the list is stored and sent as an extra to the food entry activity
         */
        lvFoodLogs.setOnItemClickListener((parent, view, position, id) -> {
            FoodLog selectedFoodLog = (FoodLog) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, FoodEntryActivity.class);
            intent.putExtra(EXTRA_FOOD_LOG, selectedFoodLog);
            intent.putExtra(EXTRA_DISPLAYED_DATE, dateDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_DAY, dayDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_MONTH, monthDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_YEAR, yearDisplayed);
            startActivity(intent);
        });
    }

    //By default, displays the data related to the current date. If another date has been selected, if goals have been updated or if new food logs were created, the data is updated
    @Override
    protected void onStart() {
        super.onStart();

        //get intent
        Intent intent = getIntent();

        //get today's date and display it
        calendar = Calendar.getInstance();
        dayToday = calendar.get(Calendar.DAY_OF_MONTH);
        monthToday = calendar.get(Calendar.MONTH);
        yearToday = calendar.get(Calendar.YEAR);

        dayDisplayed = dayToday;
        monthDisplayed = monthToday;
        yearDisplayed = yearToday;

        dateToday = dayToday + "-" + (monthToday + 1) + "-" + yearToday;
        dateNowDisplay.setText(dateToday);
        dateDisplayed = dateToday;

        //retrieve date from intent if there is any and display it
        if (intent.hasExtra(EXTRA_DISPLAYED_DATE)){
            dateDisplayed = intent.getStringExtra(EXTRA_DISPLAYED_DATE);
            dayDisplayed = intent.getIntExtra(EXTRA_DISPLAYED_DAY, dayToday);
            monthDisplayed = intent.getIntExtra(EXTRA_DISPLAYED_MONTH, monthToday);
            yearDisplayed = intent.getIntExtra(EXTRA_DISPLAYED_YEAR, yearToday);
            dateNowDisplay.setText(dateDisplayed);
        }

        //get shared preferences for profile and goals
        setupPref = getSharedPreferences(SETUP_PREF, Activity.MODE_PRIVATE);

        //retrieve goal data from shared preferences
        weightInt = setupPref.getInt(PREF_WEIGHT, 0);
        caloricGoalInt = setupPref.getInt(PREF_CALORIC_GOAL, 0);
        carbGoalInt = setupPref.getInt(PREF_CARBS, 0);
        proteinGoalInt = setupPref.getInt(PREF_PROTEIN, 0);
        fatGoalInt = setupPref.getInt(PREF_FAT, 0);
        waterGoalInt = setupPref.getInt(PREF_WATER_GOAL, 0);

        //set water count. if there is already data for it in the day data database, it is retrieved and displayed. if not, it is set to 0
        waterCounter.setCount(dayDataDHB.getIntByDate(COLUMN_WATER_INTAKE, dateDisplayed));
        waterIntake = waterCounter.getCount();

        //creates new row in day data database if no data has been entered for the date currently displayed. If data already exists, no update.
        dayDataDHB.addOneIgnore(dateDisplayed, weightInt, caloricGoalInt, carbGoalInt, proteinGoalInt, fatGoalInt, waterGoalInt, 0);

        //set up to date values for all UI elements
        setValuesTo(dateDisplayed);
    }

    /**
     * Data saved on pause in db just in case
     */
    @Override
    protected void onPause() {
        super.onPause();
        //save dayData into daydata.db
        dayDataDHB.addOneReplace(dateDisplayed, weightInt, caloricGoalInt, carbGoalInt, proteinGoalInt, fatGoalInt, waterGoalInt, waterIntake);
    }

    /**
     * Calls all the methods related to updating the UI with data from the date passed as parameter
     * @param date String of the date the values will be updated to
     */
    private void setValuesTo(String date) {
        updateCaloricCountersDisplays(date);
        updateMealDisplays(date);
        updateMacroPBs(date);
        updateWaterDisplay(date);
        updateFoodDiary(date);
    }

    /**
     * Updates all UI elements related to overall caloric intake.
     * Gathers sum of caloric intake from food logs of the day, and caloric goal from databases.
     * Calculates the amount of calories left to eat.
     * Values for caloric goal, calories eaten, calories left and circular progress bar are updated on the screen.
     * If daily goals are reached, the UI is modified (colors and texts) to notify the user.
     * @param date String of the date the values will be updated to
     */
    private void updateCaloricCountersDisplays(String date) {
        //get caloric values from databases
        caloricIntake = foodLogDBH.getCaloriesByDate(date);
        caloricGoalInt = dayDataDHB.getIntByDate(COLUMN_CALORIC_GOAL, date);
        caloriesLeftInt = caloricGoalInt - caloricIntake;
        //set caloric values to TextViews
        if (caloricIntake <= caloricGoalInt) {
            txtCaloriesLeft.setText("Left");
            caloriesLeftDisplay.setText(String.valueOf(caloriesLeftInt));
            eatenDisplay.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
            txtEatenDisplay.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        } else {
            txtCaloriesLeft.setText("Over");
            caloriesLeftDisplay.setText(String.valueOf(Math.abs(caloriesLeftInt)));
            eatenDisplay.setTextColor(getResources().getColor(R.color.orange_pastel));
            txtEatenDisplay.setTextColor(getResources().getColor(R.color.orange_pastel));

        }
        caloricGoalDisplay.setText(String.valueOf(caloricGoalInt));
        eatenDisplay.setText(String.valueOf(caloricIntake));
        //set circular caloric progress bar progress and max values
        circularPB.setMax(caloricGoalInt);
        circularPB.setProgress(caloricIntake);
    }

    /**
     * Updates progress bars for macronutrient objectives
     * Gathers macronutrient goals from day data database, then calculates values as a share of caloric goal.
     * Gathers the sum of each macronutrient intake from food logs database, then multiplies them by their caloric value
     * (carbs and proteins are worth 4 kCal per gram; fats are worth 8 kCal per gram).
     * Sets progress bar max values according to goals and progress values according to sum of daily intake.
     * Updates the UI colors if goal has been reached.
     * @param date String of the date the values will be updated to
     */
    private void updateMacroPBs (String date) {
        //get macro goals from daydata.db
        carbGoalInt = dayDataDHB.getIntByDate(COLUMN_CARBS_GOAL, date);
        proteinGoalInt = dayDataDHB.getIntByDate(COLUMN_PROTEIN_GOAL, date);
        fatGoalInt = dayDataDHB.getIntByDate(COLUMN_FAT_GOAL, date);
        //compute macro shares as percentages of caloric goal
        carbShare = (caloricGoalInt * carbGoalInt)/100;
        proteinShare = (caloricGoalInt * proteinGoalInt)/100;
        fatShare = (caloricGoalInt * fatGoalInt)/100;
        //get sum of grams per macro from foodlog.db and convert to calories
        carbsIntake = foodLogDBH.getSumByDate(COLUMN_CARBS, date) * 4;
        proteinIntake = foodLogDBH.getSumByDate(COLUMN_PROTEIN, date) * 4;
        fatIntake = foodLogDBH.getSumByDate(COLUMN_FAT, date) * 8;
        //change text color if daily limit reached
        if (carbsIntake > carbShare) {
            txtCarbs.setTextColor(getResources().getColor(R.color.orange_pastel));
        } else {
            txtCarbs.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        }
        if (proteinIntake > proteinShare) {
            txtProtein.setTextColor(getResources().getColor(R.color.orange_pastel));
        } else {
            txtProtein.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        }
        if (fatIntake > fatShare) {
            txtFat.setTextColor(getResources().getColor(R.color.orange_pastel));
        } else {
            txtFat.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        }
        //set macro progress bar max and progress values
        carbsPB.setMax(carbShare);
        proteinPB.setMax(proteinShare);
        fatPB.setMax(fatShare);
        carbsPB.setProgress(carbsIntake);
        proteinPB.setProgress(proteinIntake);
        fatPB.setProgress(fatIntake);
    }

    /**
     * Updates food diary (food log list).
     * Gets ArrayList of all food logs of the selected date from the food logs database.
     * Creates an adapter, with given style, to display the array in a list view.
     * Attaches the list to the adapter
     * @param date String of the date the values will be updated to
     */
    private void updateFoodDiary(String date) {
        //get FoodLog list from foodlog.db
        List<FoodLog> foodLogs = foodLogDBH.getFoodLog(date);
        //create adapter for FoodLog list
        ArrayAdapter<FoodLog> foodLogArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item_small, foodLogs);
        //attach list to adapter
        lvFoodLogs.setAdapter(foodLogArrayAdapter);
    }

    /**
     * Updates the UI elements that show the calories eaten per meal.
     * Gets the sums per meal from the food logs database.
     * Updates the UI accordingly.
     * @param date String of the date the values will be updated to
     */
    private void updateMealDisplays(String date) {
        //get sum of calories per meal on a given date from foodlog.db
        breakfastIntake = foodLogDBH.getCaloriesByMealByDate("breakfast", date);
        lunchIntake = foodLogDBH.getCaloriesByMealByDate("lunch", date);
        dinnerIntake = foodLogDBH.getCaloriesByMealByDate("dinner", date);
        extrasIntake = foodLogDBH.getCaloriesByMealByDate("extras", date);
        //set calories per meal values to TextViews
        breakfastDisplay.setText(String.valueOf(breakfastIntake));
        lunchDisplay.setText(String.valueOf(lunchIntake));
        dinnerDisplay.setText(String.valueOf(dinnerIntake));
        extrasDisplay.setText(String.valueOf(extrasIntake));
    }

    /**
     * Updates the water counter value.
     * Gathers the water intake and intake goal for a given date.
     * Changes the color of the value to red if the goal is not reached yet, green otherwise.
     * Updates the value in the UI.
     * @param date String of the date the values will be updated to
     */
    private void updateWaterDisplay(String date) {
        //get water intake and goal from daydata.db
        waterIntake = dayDataDHB.getIntByDate(COLUMN_WATER_INTAKE, date);
        waterGoalInt = dayDataDHB.getIntByDate(COLUMN_WATER_GOAL, date);
        //switch color of water count according to completion of daily water consumption goal
        if (waterCounter.getCount() >= waterGoalInt) {
            waterDisplay.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green));
        } else {
            waterDisplay.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
        }
        //set water intake to TextView
        waterDisplay.setText(waterCounter.getCountString());
    }

    /**
     * Inflating three dot options menu object (reading xml and making menu visible in app)
     * @param menu Three dot menu
     * @return true when the menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Define action on menu item selection.
     * Starts setup, statistics or about activities, with date displayed as an extra so that the date remains when come back to main activity.
     * @param item Menu item clicked
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemSelected = item.getItemId();
        if (itemSelected == R.id.menuItemSetup) {
            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
            intent.putExtra(EXTRA_DISPLAYED_DATE, dateDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_DAY, dayDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_MONTH, monthDisplayed);
            intent.putExtra(EXTRA_DISPLAYED_YEAR, yearDisplayed);
            startActivity(intent);
        } if (itemSelected == R.id.menuItemStats){
            Intent graphIntent = new Intent(MainActivity.this, GraphActivity.class);
            graphIntent.putExtra(EXTRA_DISPLAYED_DATE, dateDisplayed);
            graphIntent.putExtra(EXTRA_DISPLAYED_DAY, dayDisplayed);
            graphIntent.putExtra(EXTRA_DISPLAYED_MONTH, monthDisplayed);
            graphIntent.putExtra(EXTRA_DISPLAYED_YEAR, yearDisplayed);
            startActivity(graphIntent);
        } if (itemSelected == R.id.menuItemAbout) {
            Intent graphIntent = new Intent(MainActivity.this, AboutActivity.class);
            graphIntent.putExtra(EXTRA_DISPLAYED_DATE, dateDisplayed);
            graphIntent.putExtra(EXTRA_DISPLAYED_DAY, dayDisplayed);
            graphIntent.putExtra(EXTRA_DISPLAYED_MONTH, monthDisplayed);
            graphIntent.putExtra(EXTRA_DISPLAYED_YEAR, yearDisplayed);
            startActivity(graphIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays date picker dialog and handles date selection. Default value of the picker is current day.
     * Gets string of date picked and displays it. Sets date displayed variable.
     * Sets day, month and year values selected to default values for next use (the picker will start from that date next time it is used).
     * A new row in the day data database is created if it does not exist for the selected date.
     * The water counter is set to the value stored in the database if it exists, otherwise 0.
     * The whole UI is updated.
     */
    private void showDateDialog() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                (view, year, monthOfYear, dayOfMonth) -> {
                    datePicked = dayOfMonth + "-" + (monthOfYear+1) + "-" + year;
                    dateDisplayed = datePicked;
                    yearDisplayed = year;
                    monthDisplayed = monthOfYear;
                    dayDisplayed = dayOfMonth;
                    dateNowDisplay.setText(datePicked);
                    dayDataDHB.addOneIgnore(dateDisplayed, weightInt, caloricGoalInt, carbGoalInt, proteinGoalInt, fatGoalInt, waterGoalInt, 0);
                    waterCounter.setCount(dayDataDHB.getIntByDate(COLUMN_WATER_INTAKE, dateDisplayed));
                    setValuesTo(dateDisplayed);
                }, yearDisplayed, monthDisplayed, dayDisplayed);
        datePickerDialog.show();

    }

    /**
     * When the calendar button is clicked, the current day data is stored/updated into the day data database and the date picker dialog pops up
     * @param v Calendar button
     */
    @Override
    public void onClick(View v) {
        dayDataDHB.addOneReplace(dateDisplayed, weightInt, caloricGoalInt, carbGoalInt, proteinGoalInt, fatGoalInt, waterGoalInt, waterIntake);
        if (v.getId() == R.id.fabBtnDate) {
            showDateDialog();
        }
    }

    /**
     * Handles back button press. Makes sure the app is closed.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
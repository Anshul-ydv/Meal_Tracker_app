package com.example.mealtrackerapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

//importing library to handle chart data and display
import com.example.mealtrackerapp.databases.DayDataDBH;
import com.example.mealtrackerapp.databases.FoodLogDBH;
import com.example.mealtrackerapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_WATER_INTAKE;
import static com.example.mealtrackerapp.databases.DayDataDBH.COLUMN_WEIGHT;
import static com.example.mealtrackerapp.databases.FoodLogDBH.COLUMN_CALORIES;
import static com.example.mealtrackerapp.databases.FoodLogDBH.COLUMN_CARBS;
import static com.example.mealtrackerapp.databases.FoodLogDBH.COLUMN_FAT;
import static com.example.mealtrackerapp.databases.FoodLogDBH.COLUMN_PROTEIN;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_DATE;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_DAY;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_MONTH;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_YEAR;

/**
 * Statistic graphs activity. The data on calories consumed, macronutrient intake, water intake or weight are displayed in graphs,
 * showing the data either for the 7 last days or 30 last days. The MPAndroidChart library is used to make and display the graphs.
 */
public class GraphActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //constants of all the possible spinner choices
    public static final String CHOICE_CALORIES = "calories";
    public static final String CHOICE_CARBS = "carbs";
    public static final String CHOICE_PROTEIN = "protein";
    public static final String CHOICE_FAT = "fat";
    public static final String CHOICE_WATER = "water";
    public static final String CHOICE_WEIGHT = "weight";
    public static final String CHOICE_WEEKLY = "weekly";
    public static final String CHOICE_MONTHLY = "monthly";

    //calling the databases
    FoodLogDBH foodLogDBH = new FoodLogDBH(GraphActivity.this);
    DayDataDBH dayDataDBH = new DayDataDBH(GraphActivity.this);

    //initiating variables
    Spinner timeChoiceSpinner, dataChoiceSpinner;
    ArrayList<String> timeChoiceArray, dataChoiceArray, xLabels;
    int numberOfDays;
    String columnNeeded, txt, itemSelected, dataSelected;
    TextView txtDescription;

// reference for MPAndroidChart: https://github.com/PhilJay/MPAndroidChart
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries;

    /**
     * References to UI elements, creation of spinners and corresponding array lists and adapters.
     * Setting up function for the show button.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //refer TextView
        txtDescription = findViewById(R.id.txtGraphDescription);

        //refer bar chart
        barChart = findViewById(R.id.barChart);

        //refer spinners
        timeChoiceSpinner = findViewById(R.id.spinnerTimeChoice);
        dataChoiceSpinner = findViewById(R.id.spinnerDataChoice);

        //set click listeners for spinners
        timeChoiceSpinner.setOnItemSelectedListener(this);
        dataChoiceSpinner.setOnItemSelectedListener(this);

        //creating ArrayList with time period choices
        timeChoiceArray = new ArrayList<>();
        timeChoiceArray.add(CHOICE_WEEKLY);
        timeChoiceArray.add(CHOICE_MONTHLY);

        //creating ArrayList with meal choices
        dataChoiceArray = new ArrayList<>();
        dataChoiceArray.add(CHOICE_CALORIES);
        dataChoiceArray.add(CHOICE_CARBS);
        dataChoiceArray.add(CHOICE_PROTEIN);
        dataChoiceArray.add(CHOICE_FAT);
        dataChoiceArray.add(CHOICE_WATER);
        dataChoiceArray.add(CHOICE_WEIGHT);

        //setting up ArrayAdapter for time choice spinner
        ArrayAdapter<String> timeSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_center, timeChoiceArray);
        timeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeChoiceSpinner.setAdapter(timeSpinnerAdapter);

        //setting up arrayAdapter for spinner with elements of the arrayList and attaching adapter to spinner object
        ArrayAdapter<String> mealSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_center, dataChoiceArray);
        mealSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataChoiceSpinner.setAdapter(mealSpinnerAdapter);

        //on Click, the entries for the bars of the chart are retrieved from the databases. then the UI is updated to show the data required by the user.
        findViewById(R.id.btnShow).setOnClickListener(v -> {
            barEntries = getBarEntries(columnNeeded, numberOfDays);
            updateGraphDisplay(numberOfDays, getBarEntries(columnNeeded, numberOfDays));
            updateDescription();
        });
    }

    /**
     * Default values for the graphs are set to calories for the last 7 days.
     * The UI is updated accordingly
     */
    @Override
    protected void onStart() {
        super.onStart();
        columnNeeded = COLUMN_CALORIES;
        numberOfDays = 7;
        dataSelected = dataChoiceSpinner.getSelectedItem().toString();
        updateGraphDisplay(numberOfDays, getBarEntries(columnNeeded, numberOfDays));
        updateDescription();
    }

    /**
     * On up button press (home), making sure that the date is sent back to the main
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentMain = new Intent(GraphActivity.this, MainActivity.class);
                intentMain.putExtra(EXTRA_DISPLAYED_DATE, getIntent().getStringExtra(EXTRA_DISPLAYED_DATE));
                intentMain.putExtra(EXTRA_DISPLAYED_DAY, getIntent().getIntExtra(EXTRA_DISPLAYED_DAY, Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
                intentMain.putExtra(EXTRA_DISPLAYED_MONTH, getIntent().getIntExtra(EXTRA_DISPLAYED_MONTH, Calendar.getInstance().get(Calendar.MONTH)));
                intentMain.putExtra(EXTRA_DISPLAYED_YEAR, getIntent().getIntExtra(EXTRA_DISPLAYED_YEAR, Calendar.getInstance().get(Calendar.YEAR)));
                startActivity(intentMain);
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    /**
     * The graph description text view is adjusted to the options selected by the user. It tells what the graph displays and in which units.
     */
    private void updateDescription() {
        switch (columnNeeded) {
            case COLUMN_CALORIES:
            case COLUMN_CARBS:
            case COLUMN_PROTEIN:
            case COLUMN_FAT:
                txt = "Daily " + dataSelected + " in kcal for the last " + numberOfDays + " days";
                txtDescription.setText(txt);
                break;
            case COLUMN_WATER_INTAKE:
                txt = "Daily " + dataSelected + " glasses for the last " + numberOfDays + " days";
                txtDescription.setText(txt);
            case COLUMN_WEIGHT:
                txt = "Daily " + dataSelected + " in kg for the last " + numberOfDays + " days";
                txtDescription.setText(txt);
        }
    }

    /**
     * Updating the data displayed in the graph.
     * The previously set bar entry value array is converted to bar data and set to the actual bar representations.
     * Labels for the x axis are created according to the number of days to be represented:
     * first letter of the day of the week if 7 days are needed/ days of the month if 30 days are to be displayed.
     * @param numberOfDays number of days displayed on the graph
     */
    private void updateGraphDisplay(int numberOfDays, ArrayList<BarEntry> barEntries) {
        //converting bar entry array to bar data set then to bar data and attaching it to the chart
        barDataSet = new BarDataSet(barEntries, "");
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        //creation of x axis labels
        xLabels = new ArrayList<>();

        //if 7 days are represented, x labels will contain 7 letters representing the days of a week.
        //that array is then converted into labels for the x axis
        if (numberOfDays == 7) {
            xLabels.clear();
            for (int i = -numberOfDays + 1; i <= 0; i++) {
                xLabels.add(getFirstLetter(getDayOfWeek(i)));
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
                barDataSet.setValueTextSize(10f);
            }
        //if 30 days are represented, x labels will contain 30 numbers (strings) representing the days of a month.
        //that array is then converted into labels for the x axis
        } else if (numberOfDays == 30) {
            xLabels.clear();
            for (int i = -numberOfDays + 1; i <= 0; i++) {
                xLabels.add(getDayOfMonth(i));
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
                barDataSet.setValueTextSize(5f);
            }
            //adapting the size of x labels to display more values
            barChart.getXAxis().setTextSize(1f);
            //making sure that all labels are displayed
            barChart.getXAxis().setLabelCount(xLabels.size());
        }
        //bar chart customization
        barChart.setFitBars(true);
        //removing gridlines and some labels
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        //setting the bar chart colors
        barDataSet.setColor(Color.rgb(249, 155, 130));
        barDataSet.setValueTextColor(Color.BLACK);
        //updating chart display
        barChart.invalidate();
    }

    /**
     * Creating an array list with the required data from the databases. The data is stored in bar entries consisting of an x/y pair of values.
     * x is the position of the bar and y is the value in calories, water glasses or weight to be displayed.
     * @param column Column name of the database
     * @param numberOfDays number of days/bar entries needed
     * @return ArrayList of bar entries with x/y pairs of value. x is the position of the bar and y is the value in calories, water glasses or weight to be displayed.
     */
    private ArrayList<BarEntry> getBarEntries(String column, int numberOfDays) {

        //getting an array list with the date strings of the 7 or 30 last days, today being the last element of the array list
        ArrayList<String> dates = new ArrayList<>();
        for (int i = -numberOfDays + 1; i <= 0; i++) {
            dates.add(getDateString(i));
        }

        //new array list for the bar entries
        barEntries = new ArrayList<>();

        //population the bar entry array list according to the data required. autoincrementation of the x position value of the bar
        //getting the sum of calories for the required days. the sum is calculated from the food logs database
        switch (column) {
            case COLUMN_CALORIES: {
                float xAxis = 0f;
                for (String date : dates) {
                    barEntries.add(new BarEntry(xAxis, foodLogDBH.getSumByDate(column, date)));
                    xAxis += 1;
                }
                //getting the sum of calories for carbs and protein for the required days. the amount in grams stored in the food logs db needs to be multiplied by 4 to represent caloric value
                break;
            }
            case COLUMN_CARBS:
            case COLUMN_PROTEIN: {
                float xAxis = 0f;
                for (String date : dates) {
                    barEntries.add(new BarEntry(xAxis, (foodLogDBH.getSumByDate(column, date)) * 4));
                    xAxis += 1;
                }
                //getting the sum of calories for fat for the required days. the amount in grams stored in the food logs db needs to be multiplied by 8 to represent caloric value
                break;
            }
            case COLUMN_FAT: {
                float xAxis = 0f;
                for (String date : dates) {
                    barEntries.add(new BarEntry(xAxis, (foodLogDBH.getSumByDate(column, date)) * 8));
                    xAxis += 1;
                }
                //getting values for water intake and weight per day from the day data database
                break;
            }
            case COLUMN_WATER_INTAKE:
            case COLUMN_WEIGHT: {
                float xAxis = 0f;
                for (String date : dates) {
                    barEntries.add(new BarEntry(xAxis, dayDataDBH.getIntByDate(column, date)));
                    xAxis += 1;
                }
                break;
            }
        }
        return barEntries;
    }

    /**
     * Getting a string for a date, given an integer representing the day difference with regards to today.
     * @param daySwitch Integer representing the day difference with regards to today. 0 is today, -1 is yesterday, 1 is tomorrow
     * @return return a string in the format dd-mm-yy
     */
    public String getDateString(int daySwitch) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daySwitch);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        return day + "-" + (month+1) + "-" + year;
    }

    /**
     * Getting the day of the week in letter format (e.g. Monday).
     * @param daySwitch Integer representing the day difference with regards to today. 0 is today, -1 is yesterday, 1 is tomorrow
     * @return return a string of day of the week (Monday, Tuesday, etc.)
     */
    public String getDayOfWeek(int daySwitch) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daySwitch);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }

    /**
     * Getting the day of the month in number format (1-31).
     * @param daySwitch Integer representing the day difference with regards to today. 0 is today, -1 is yesterday, 1 is tomorrow
     * @return return a string of day of the month (1-31)
     */
    public String getDayOfMonth(int daySwitch) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daySwitch);
        return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Extracts the first letter of a word
     * @param word String from which the first letter is extracted
     * @return first letter of the word
     */
    public String getFirstLetter(String word){
        return String.valueOf(word.charAt(0));
    }

    /**
     * Handling click event on items of both spinners.
     * Clicking an item in the data choice spinner stores the value of that item in a dataSelected variable and determines the column needed from one of databases.
     * Clicking an item in the time choice spinner sets the numberOfDays value to 7 or 30.
     * Those values can then be used to create the bar entries by getting data from the day data and food log databases.
     * @param parent Spinner adapter
     * @param view Spinner
     * @param position Position of the selected item in the list
     * @param id id of the selected item
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        itemSelected = (String) parent.getItemAtPosition(position);

        //clicking an item in the data choice spinner stores the value of that item in a dataSelected variable and determines the column needed from one of databases.
        switch (parent.getId()) {
            case R.id.spinnerDataChoice:
                dataSelected = itemSelected;
                switch (itemSelected) {
                    case CHOICE_CALORIES:
                        columnNeeded = COLUMN_CALORIES;
                        break;
                    case CHOICE_CARBS:
                        columnNeeded = COLUMN_CARBS;
                        break;
                    case CHOICE_PROTEIN:
                        columnNeeded = COLUMN_PROTEIN;
                        break;
                    case CHOICE_FAT:
                        columnNeeded = COLUMN_FAT;
                        break;
                    case CHOICE_WATER:
                        columnNeeded = COLUMN_WATER_INTAKE;
                        break;
                    case CHOICE_WEIGHT:
                        columnNeeded = COLUMN_WEIGHT;
                        break;
                }
                break;
                //clicking an item in the time choice spinner sets the numberOfDays value to 7 or 30
            case R.id.spinnerTimeChoice:
                switch (itemSelected) {
                    case CHOICE_WEEKLY:
                        numberOfDays = 7;
                        break;
                    case CHOICE_MONTHLY:
                        numberOfDays = 30;
                        break;
                }
                break;
        }
    }

    /**
     * What is done when nothing is selected in the spinner
     * @param parent spinner adapter
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
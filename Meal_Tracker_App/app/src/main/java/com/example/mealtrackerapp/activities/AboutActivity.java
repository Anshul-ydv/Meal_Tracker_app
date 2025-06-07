package com.example.mealtrackerapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mealtrackerapp.R;

import java.util.Calendar;

import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_DATE;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_DAY;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_MONTH;
import static com.example.mealtrackerapp.activities.MainActivity.EXTRA_DISPLAYED_YEAR;


public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    /**
     * if Home button is pressed
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentMain = new Intent(AboutActivity.this, MainActivity.class);
                intentMain.putExtra(EXTRA_DISPLAYED_DATE, getIntent().getStringExtra(EXTRA_DISPLAYED_DATE));
                intentMain.putExtra(EXTRA_DISPLAYED_DAY, getIntent().getIntExtra(EXTRA_DISPLAYED_DAY, Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
                intentMain.putExtra(EXTRA_DISPLAYED_MONTH, getIntent().getIntExtra(EXTRA_DISPLAYED_MONTH, Calendar.getInstance().get(Calendar.MONTH)));
                intentMain.putExtra(EXTRA_DISPLAYED_YEAR, getIntent().getIntExtra(EXTRA_DISPLAYED_YEAR, Calendar.getInstance().get(Calendar.YEAR)));
                startActivity(intentMain);
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }
}
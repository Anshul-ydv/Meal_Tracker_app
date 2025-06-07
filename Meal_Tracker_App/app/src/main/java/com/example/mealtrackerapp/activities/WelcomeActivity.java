package com.example.mealtrackerapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.example.mealtrackerapp.R;

/**
 * Introduction activity. Only displayed on first launch.
 */
public class WelcomeActivity extends AppCompatActivity {
    Button buttonSetup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_welcome);

        //button to setup activity
        buttonSetup=findViewById(R.id.button);
        buttonSetup.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this,SetupActivity.class)));
        }

    /**
     * Making sure that back button leaves the app and does not go back to main activity
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ZoneChoiceActivity extends AppCompatActivity {

    //Declare buttons
    Button centerButton, frontLeftButton, frontRightButton, backLeftButton, backRightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_choice);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init buttons
        centerButton = findViewById(R.id.centerButton);
        frontLeftButton = findViewById(R.id.frontLeftButton);
        frontRightButton = findViewById(R.id.frontRightButton);
        backLeftButton = findViewById(R.id.backLeftButton);
        backRightButton = findViewById(R.id.backRightButton);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void onSelectZone(View view){

        Intent i = new Intent(this, SpeakerPlayingActivity.class);
        startActivity(i);
    }
}

package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

public class SelectZone extends AppCompatActivity {
    float position;
    Button setLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Location");
        setLocation = findViewById(R.id.nextBtn);


        Croller croller = (Croller) findViewById(R.id.croller);
        croller.setIndicatorWidth(10);

        croller.setStartOffset(0);
        croller.setMin(0);
        croller.setMax(100);
        croller.setLabel("");
        croller.setProgressPrimaryColor(Color.parseColor("#BCA9E6"));
        croller.setIndicatorColor(Color.parseColor("#BCA9E6"));
        croller.setProgressSecondaryCircleSize(7);
        croller.setProgressSecondaryColor(Color.parseColor("#33ffffff"));
        croller.setProgressPrimaryCircleSize(7);
        croller.setSweepAngle(360);
        Log.d("SelectZoneAngle", String.valueOf(croller.getProgress()));


        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onStartTrackingTouch(Croller croller) {
                position = 0;
            }

            @Override
            public void onProgressChanged(Croller croller, int progress) {
                position = progress;
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {}
        });

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectZone.this, SpeakerPlayingActivity.class);
                position = position/100;
                i.putExtra("position", position);
                startActivity(i);
            }
        });
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
}

package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

public class SelectZone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Croller croller = (Croller) findViewById(R.id.croller);
        croller.setIndicatorWidth(10);

        croller.setStartOffset(0);
        croller.setMin(0);
        croller.setMax(100);
        croller.setLabel("");
        croller.setProgressPrimaryColor(Color.parseColor("#BCA9E6"));
        croller.setIndicatorColor(Color.parseColor("#BCA9E6"));
        croller.setProgressSecondaryCircleSize(3);
        croller.setProgressSecondaryColor(R.color.alertDialogBackground);
        croller.setProgressPrimaryCircleSize(5);
        croller.setSweepAngle(360);
        Log.d("SelectZoneAngle", String.valueOf(croller.getProgress()));


        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {

            @Override
            public void onStartTrackingTouch(Croller croller) {

            }

            @Override
            public void onProgressChanged(Croller croller, int progress) {

            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped
            }
        });
    }

    public void setLocation(View view) {
        Intent i = new Intent(SelectZone.this, SpeakerPlayingActivity.class);

        //TODO - make sure to putExtra the position / progress
        startActivity(i);
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

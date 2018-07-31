package com.example.bertogonz3000.surround;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

public class SelectZone extends AppCompatActivity {
    float position;
    ImageButton setLocation;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setLocation = findViewById(R.id.nextBtn);

        Croller croller = (Croller) findViewById(R.id.croller);
        croller.setIndicatorWidth(10);

        croller.setStartOffset(0);
        croller.setMin(0);
        croller.setMax(100);
        croller.setLabel("");
        croller.setProgressPrimaryColor(Color.parseColor("#BCA9E6"));
        croller.setIndicatorColor(Color.parseColor("#BCA9E6"));
        croller.setProgressSecondaryCircleSize(3);
        croller.setProgressSecondaryColor(Color.parseColor("#33ffffff"));
        croller.setProgressPrimaryCircleSize(5);
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
            Handler handle = new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    progressDialog.incrementProgressBy(10); // Incremented By Value 10
                }
            };

            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(SelectZone.this, R.style.CustomDialog);
                progressDialog.setMax(100); // Progress Dialog Max Value
                progressDialog.setMessage("Loading Track..."); // Setting Message
                progressDialog.setTitle("Downloading"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
                progressDialog.show(); // Display Progress Dialog

                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (progressDialog.getProgress() < progressDialog.getMax()) {
                                Thread.sleep(200);
                                handle.sendMessage(handle.obtainMessage());
                            }
                            if (progressDialog.getProgress() == progressDialog.getMax()) {
                                progressDialog.dismiss();
                                setLocation();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public void setLocation() {

        Intent i = new Intent(this, SpeakerPlayingActivity.class);
        position = position/100;
        i.putExtra("position", position);
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

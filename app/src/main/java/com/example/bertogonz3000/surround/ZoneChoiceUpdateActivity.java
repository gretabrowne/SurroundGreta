package com.example.bertogonz3000.surround;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import it.beppi.knoblibrary.Knob;

public class ZoneChoiceUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_choice_update);
        final Knob knob4 = (Knob) findViewById(R.id.knob4);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final TextView textView4 = (TextView) findViewById(R.id.textView4);
        int number = knob4.getState() + 1;
        textView4.setText(Integer.toString(number));
        knob4.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                int number = knob4.getState() + 1;
                textView4.setText(Integer.toString(number));
            }
        });



    }

    public void setLocation() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
//            case R.id.next: {
//                Intent intent = new Intent(ZoneChoiceUpdateActivity.this, SpeakerPlayingActivity.class);
//                startActivity(intent);
//                return true;
//            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import it.beppi.knoblibrary.Knob;

public class ZoneChoiceUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_choice_update);
        final Knob knob4 = (Knob) findViewById(R.id.knob4);


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

        Intent i = new Intent(ZoneChoiceUpdateActivity.this, SpeakerPlayingActivity.class);
        i.putExtra("position", Integer.valueOf(textView4.getText().toString())); //todo-- find shorter way to convert this??
    }
}

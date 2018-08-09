package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class LandingActivity extends AppCompatActivity {

    //declare Buttons
    ImageButton speakerButton, controllerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //init Buttons
        speakerButton = findViewById(R.id.speakerButton);
        controllerButton = findViewById(R.id.controllerButton);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.settings_button){
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return true;
    }

    //Become a speaker
    public void selectSpeaker(View view){
        //New intent between this and Zone Choice
        Intent i = new Intent(this, SelectZone.class);
        startActivity(i);
    }

    //Become the controller
    public void selectController(View view){
        Intent i = new Intent(this, SongSelectionActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Log.d("test", "back pressed");
    }
}

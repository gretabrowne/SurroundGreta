package com.example.bertogonz3000.surround;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.parse.ParseLiveQueryClient;

public class LostConnectionActivity extends AppCompatActivity {

    ParseLiveQueryClient parseLiveQueryClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_connection);
        parseLiveQueryClient = getIntent().getParcelableExtra("livequeryclient");

    }

    public void checkConnection(View view) {
        //TODO - check if the connection to the server is valid
        Intent intent = new Intent();
        setResult(22, intent);
    }

}

package com.example.bertogonz3000.surround;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.bertogonz3000.surround.ParseModels.AudioIDs;
import com.example.bertogonz3000.surround.ParseModels.PlayPause;
import com.example.bertogonz3000.surround.ParseModels.Session;
import com.example.bertogonz3000.surround.ParseModels.Throwing;
import com.example.bertogonz3000.surround.ParseModels.Time;
import com.example.bertogonz3000.surround.ParseModels.Volume;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application{

    private String ipAddress, appID;

    @Override
    public void onCreate() {
        super.onCreate();

        getPrefs();

//        ParseObject.registerSubclass(Song.class);
        ParseObject.registerSubclass(AudioIDs.class);
        ParseObject.registerSubclass(PlayPause.class);
        ParseObject.registerSubclass(Session.class);
        ParseObject.registerSubclass(Throwing.class);
        ParseObject.registerSubclass(Time.class);
        ParseObject.registerSubclass(Volume.class);

        //troubleshooting
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        //monitoring OKHttp traffic
        //can be Level.BASIC, Level.HEADERS, or Level.BODY
        //see http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

       //   Init the Parse Server (Hannah's server)
//        Parse.initialize(new Parse.Configuration.Builder(this)
//                .applicationId("SurroundId")
//                .clientKey(null)
//                .clientBuilder(builder)
//                .server("http://172.21.79.146:1337/parse").build());

      //  Init the Parse Server (Berto's server)
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(appID)
                .clientKey(null)
                .clientBuilder(builder)
                .server("http://" + ipAddress + ":1337/parse").build());

//        //Init the Parse Server (Greta's server)
//        Parse.initialize(new Parse.Configuration.Builder(this)
//        .applicationId("bghsurround")
//        .clientKey(null)
//        .clientBuilder(builder)
//        .server("http://172.21.74.193:1337/parse").build());

    }

    private void getPrefs(){
        SharedPreferences serverPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ipAddress = serverPrefs.getString("ipaddress", "172.21.70.129");
        appID = serverPrefs.getString("appid", "SurroundSound47");
        //Toast.makeText(this, "appID = " + appID, Toast.LENGTH_SHORT).show();
    }
}

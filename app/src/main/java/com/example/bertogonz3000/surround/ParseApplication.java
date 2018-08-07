package com.example.bertogonz3000.surround;

import android.app.Application;

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

    @Override
    public void onCreate() {
        super.onCreate();

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


      //  Init the Parse Server (Hannah's server)
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("SurroundSound47")
                .clientKey(null)
                .clientBuilder(builder)
                .server("http://172.21.70.129:1337/parse").build());

//        //Init the Parse Server (Greta's server)
//        Parse.initialize(new Parse.Configuration.Builder(this)
//        .applicationId("bghsurround")
//        .clientKey(null)
//        .clientBuilder(builder)
//        .server("http://172.21.74.193:1337/parse").build());

    }
}

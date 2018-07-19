package com.example.bertogonz3000.surround;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //troubelshooting
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        //monitoring OKHttp traffic
        //can be Level.BASIC, Level.HEADERS, or Level.BODY
        //see http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);



        //Init the Parse Server
        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("bghsurround")
        .clientKey(null)
        .clientBuilder(builder)
        .server("http://172.21.69.54:1337/parse").build());

    }
}

package com.example.bertogonz3000.surround.ParseModels;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Time")
public class Time extends ParseObject{

    //Getters
    public String getTimeID(){return getObjectId();}

    public int getTime(){return getInt("Time");}


    //Setters
    public void setTime(int time){put("Time", time);}
}
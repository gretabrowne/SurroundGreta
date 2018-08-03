package com.example.bertogonz3000.surround.ParseModels;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Volume")
public class Volume extends ParseObject{

    //Getters
    public String getVolumeID(){return getObjectId();}

    //TODO -- Change Volume to a float instead of an integer
    public int getVolume(){return getInt("volume");}


    //Setters
    public void setVolume(int volume){put("volume", volume);}
}
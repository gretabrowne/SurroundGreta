package com.example.bertogonz3000.surround.ParseModels;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Throwing")
public class Throwing extends ParseObject{

    //Getters
    public String getThrowingID(){return getObjectId();}

    public boolean getThrowing(){return getBoolean("throwing");}

    public float getLocation(){return (float) getNumber("location");}

    //Setters
    public void setThrowing(boolean throwing){put("throwing", throwing);}

    public void setLocation(float location){put("location", location);}
}
package com.example.bertogonz3000.surround.ParseModels;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Throwing")
public class Throwing extends ParseObject{

    //Getters
    public String getThrowingID(){return getObjectId();}

    public boolean getThrowing(){return getBoolean("throwing");}

    public float getLocation(){return (float) getDouble("location");}

    public int getControllerNumber () {return getInt("controllerNumber");}

    //Setters
    public void setThrowing(boolean throwing){put("throwing", throwing);}

    public void setLocation(float location){put("location", location);}

    public void setControllerNumber (int controllerNumber) {put("controllerNumber", controllerNumber);}
}
package com.example.bertogonz3000.surround.ParseModels;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("AudioIDs")
public class AudioIDs extends ParseObject{

    //Getters
    public String getAudioID(){return getObjectId();}

    public List<Integer> getIDs(){return getList("IDs");}

    public int getControllerNumber () {return getInt("controllerNumber");}

    //Setters
    public void setIDs(List<Integer> list){put("IDs", list);}
    public void setControllerNumber (int controllerNumber) {put("controllerNumber", controllerNumber);}
}
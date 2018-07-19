package com.example.bertogonz3000.surround;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Song")
public class Song extends ParseObject{

    public boolean getIsPlaying(){
       return getBoolean("isPlaying");
    }

    public int getFileId(){
       return getInt("audioFile");
    }

    public float getVolume(){
        return (float) getDouble("univVol");
    }

    public void setIsPlaying(boolean isPlaying){
        put("isPlaying", isPlaying);
    }

    public void setFileId(Integer fileId){
        put("audioFile", fileId);
    }

    public void setVolume(float volume){
        put("univVol", volume);
    }

}

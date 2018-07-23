package com.example.bertogonz3000.surround;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Song")
public class Song extends ParseObject{

    public boolean getIsPlaying(){
       return getBoolean("isPlaying");
    }

    public float getVolume(){
        return (float) getDouble("univVol");
    }

    public void setIsPlaying(boolean isPlaying){
        put("isPlaying", isPlaying);
    }

    public void setVolume(float volume){
        put("univVol", volume);
    }

    public void setAudioIds(List<Integer> list){
        put("audioIds", list);
    }

    public List<Integer> getAudioIds(){
      return getList("audioIds");
    }

    public void setTime(int time) {put("currentTime", time); }

    public int getTime() { return (int) getInt("currentTime"); }

}

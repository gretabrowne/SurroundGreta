package com.example.bertogonz3000.surround.ParseModels;


import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Session")
public class Session  extends ParseObject{

    //Session, playpause, throwing(contains location), volume, audioIDs, tracks, time
//    public Session(String playPauseID, String timeID, String throwingID, String volumeID, String audioID) {
//        put("playPause", playPauseID);
//        put("time", timeID);
//        put("throwing", throwingID);
//        put("volume", volumeID);
//        put("audioIDs", audioID);
//    }

    //Getters

    public AudioIDs getAudioIDs() {
        return (AudioIDs) get("audioIDs");
    }
    public PlayPause getPlayPause() {
        return (PlayPause) get("playPause");
    }
    public Volume getVolume() {
        return (Volume) get("volume");
    }
    public Time getTimeObject() {
        return (Time) getParseObject("audioIDs");
    }
    public Throwing getThrowingObject() {
        return (Throwing) get("throwing");
    }

    public List<Integer> getIDs(){return getList("IDs");}


    //Setters
    public void setIDs(List<Integer> list){put("IDs", list);}

    //Setters
    public void setPlayPause(PlayPause playPause){put("playPause", playPause);}

    public void setTimeObject(Time time){put("time", time);}

    public void setThrowing(Throwing throwing){put("throwing", throwing);}

    public void setVolume(Volume volume){put("volume", volume);}

    public void setAudio(AudioIDs audio){put("audioIDs", audio);}
}
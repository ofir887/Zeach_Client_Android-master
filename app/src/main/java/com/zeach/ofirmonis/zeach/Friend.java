package com.zeach.ofirmonis.zeach;

import java.io.Serializable;

/**
 * Created by ofirmonis on 23/06/2017.
 */

public class Friend implements Serializable{
    private String Name;
    private String UID;
    private String PhotoUrl;
    private String CurrentBeach;

    public Friend(){

    }

    public Friend(String name, String UID,String photoUrl,String currentBeach) {
        this.Name = name;
        this.UID = UID;
        this.PhotoUrl = photoUrl;
        this.CurrentBeach = currentBeach;
    }

    public String getCurrentBeach() {
        return CurrentBeach;
    }

    public void setCurrentBeach(String currentBeach) {
        CurrentBeach = currentBeach;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "Name='" + Name + '\'' +
                ", UID='" + UID + '\'' +
                ", PhotoUrl='" + PhotoUrl + '\'' +
                '}';
    }
}



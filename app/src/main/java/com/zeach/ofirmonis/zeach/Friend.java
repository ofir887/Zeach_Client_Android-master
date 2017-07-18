package com.zeach.ofirmonis.zeach;

import java.io.Serializable;

/**
 * Created by ofirmonis on 23/06/2017.
 */

public class Friend implements Serializable{
    private String Name;
    private String UID;

    public Friend(String name, String UID) {
        Name = name;
        this.UID = UID;

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


}



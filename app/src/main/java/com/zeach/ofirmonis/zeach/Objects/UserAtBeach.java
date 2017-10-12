package com.zeach.ofirmonis.zeach.Objects;

/**
 * Created by ofirmonis on 12/10/2017.
 */

public class UserAtBeach {
    private String mBeachName;
    private String mBeachID;

    public UserAtBeach(String mBeachName, String mBeachID) {
        this.mBeachName = mBeachName;
        this.mBeachID = mBeachID;
    }

    public String getmBeachName() {
        return mBeachName;
    }

    public void setmBeachName(String mBeachName) {
        this.mBeachName = mBeachName;
    }

    public String getmBeachID() {
        return mBeachID;
    }

    public void setmBeachID(String mBeachID) {
        this.mBeachID = mBeachID;
    }
}

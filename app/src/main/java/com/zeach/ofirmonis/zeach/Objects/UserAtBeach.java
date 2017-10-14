package com.zeach.ofirmonis.zeach.Objects;

import java.sql.Timestamp;

/**
 * Created by ofirmonis on 12/10/2017.
 */

public class UserAtBeach {
    private String mBeachName;
    private String mBeachID;
    private long mTimeStamp;

    public UserAtBeach(String mBeachName, String mBeachID, long timestamp) {
        this.mBeachName = mBeachName;
        this.mBeachID = mBeachID;
        this.mTimeStamp = timestamp;
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

    public long getmTimeStamp() {
        return mTimeStamp;
    }
}

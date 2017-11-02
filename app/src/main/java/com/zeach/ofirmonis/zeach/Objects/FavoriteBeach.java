package com.zeach.ofirmonis.zeach.Objects;

/**
 * Created by ofirmonis on 21/10/2017.
 */

public class FavoriteBeach {
    private String mBeachKey;
    private String mBeachName;
    private String mBeachCountry;

    public FavoriteBeach(String mBeachKey, String mBeachName, String mBeachCountry) {
        this.mBeachKey = mBeachKey;
        this.mBeachName = mBeachName;
        this.mBeachCountry = mBeachCountry;
    }

    public String getmBeachKey() {
        return mBeachKey;
    }

    public void setmBeachKey(String mBeachKey) {
        this.mBeachKey = mBeachKey;
    }

    public String getmBeachName() {
        return mBeachName;
    }

    public void setmBeachName(String mBeachName) {
        this.mBeachName = mBeachName;
    }

    public String getmBeachCountry() {
        return mBeachCountry;
    }

    public void setmBeachCountry(String mBeachCountry) {
        this.mBeachCountry = mBeachCountry;
    }

    @Override
    public String toString() {
        return "FavoriteBeach{" +
                "mBeachKey='" + mBeachKey + '\'' +
                ", mBeachName='" + mBeachName + '\'' +
                ", mBeachCountry='" + mBeachCountry + '\'' +
                '}';
    }
}

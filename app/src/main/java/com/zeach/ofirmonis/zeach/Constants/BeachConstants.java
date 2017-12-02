package com.zeach.ofirmonis.zeach.Constants;

import android.graphics.Color;

/**
 * Created by ofirmonis on 01/12/2017.
 */

public class BeachConstants {

    public static final String HIGH_TRAFFIC = "High";

    public static final String MEDIUM_TRAFFIC = "Medium";

    public static final String LOW_TRAFFIC = "Low";

    private static final int TRANSPARENT = 40;

    public static int getTrafficColorByString(String aTrafficState) {
        switch (aTrafficState) {
            case HIGH_TRAFFIC:
                //return Color.RED;
                return Color.argb(TRANSPARENT, 255, 0, 0);
            case MEDIUM_TRAFFIC:
                return Color.argb(TRANSPARENT, 255, 140, 0);
            case LOW_TRAFFIC:
                return Color.argb(TRANSPARENT, 0, 255, 0);
            default:
                return Color.TRANSPARENT;
        }
    }

}

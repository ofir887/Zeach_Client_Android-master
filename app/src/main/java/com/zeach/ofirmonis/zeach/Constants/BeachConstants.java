package com.zeach.ofirmonis.zeach.Constants;

import android.graphics.Color;

/**
 * Created by ofirmonis on 01/12/2017.
 */

public class BeachConstants {

    public static final String HIGH_TRAFFIC = "High";

    public static final String MEDIUM_TRAFFIC = "Medium";

    public static final String LOW_TRAFFIC = "Low";

    public static int getTrafficColorByString(String aTrafficState) {
        switch (aTrafficState) {
            case HIGH_TRAFFIC:
                return Color.RED;
            case MEDIUM_TRAFFIC:
                return Color.YELLOW;
            case LOW_TRAFFIC:
                return Color.GREEN;
            default:
                return Color.TRANSPARENT;
        }
    }

}

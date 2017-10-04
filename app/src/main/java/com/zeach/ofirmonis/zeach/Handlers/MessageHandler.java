package com.zeach.ofirmonis.zeach.Handlers;

import android.os.Handler;
import android.os.Message;

import com.zeach.ofirmonis.zeach.Constants.GpsConstants;

/**
 * Created by ofirmonis on 04/10/2017.
 */

public class MessageHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
        //super.handleMessage(msg);
        int state = msg.arg1;
        switch (state) {
            case GpsConstants.GET_GPS_COORDINATES: {
                break;
            }
            case GpsConstants.GET_USER_COUNTRY:
                break;
        }

    }
}

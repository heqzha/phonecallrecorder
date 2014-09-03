package com.humming.heqzha.phonecallrecorder;

import android.app.Application;

import com.humming.heqzha.phonecallrecorder.library.FixedSizeQueue;

import java.text.SimpleDateFormat;

/**
 * Defines global variable for all components.
 * Created by heqzha on 14-8-30.
 */
public class RecorderApplication extends Application {

    //Data format. Example: 2000-01-01 12:00:00
    public static final SimpleDateFormat DATAFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //Store the phone call status.
    private FixedSizeQueue gPhoneStateQ;

    public final FixedSizeQueue getPhoneStateQueue() {
        return gPhoneStateQ;
    }

    public void setPhoneStateQueue(final FixedSizeQueue queue){
        gPhoneStateQ = queue;
    }

    @Override
    public void onCreate() {
        gPhoneStateQ = new FixedSizeQueue(3);
    }
}

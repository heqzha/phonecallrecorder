package com.humming.heqzha.phonecallrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {
    public PhoneStateReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("PhoneStateReceiver.onReceive", "Start");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            String incomingNumber = extras
                    .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.w("PhoneStateReceiver.onReceive", "Phone State: " + state);
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                PhoneCallService.startActionPhoneStateChangedIdle(context, incomingNumber, "Incoming");
            }else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
                PhoneCallService.startActionPhoneStateChangedRinging(context, incomingNumber, "Incoming");
            }else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)){
                PhoneCallService.startActionPhoneStateChangedOffhook(context, incomingNumber, "Incoming");
            }
        }
        Log.d("PhoneStateReceiver.onReceive", "Done");
    }
}

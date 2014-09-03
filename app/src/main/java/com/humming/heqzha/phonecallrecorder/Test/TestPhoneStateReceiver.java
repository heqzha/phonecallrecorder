package com.humming.heqzha.phonecallrecorder.Test;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * Used to test phone state broadcast receiver.
 * Created by heqzha on 14-9-3.
 */
public class TestPhoneStateReceiver {

    public static void testSendAction(Context context, String state){
        final String action = "android.intent.action.TEST_PHONE_STATE";
        Intent intent = new Intent(action);
        intent.putExtra(TelephonyManager.EXTRA_STATE, state);
        intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, "123");
        context.sendBroadcast(intent);
    }

}

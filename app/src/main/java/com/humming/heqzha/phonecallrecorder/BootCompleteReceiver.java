package com.humming.heqzha.phonecallrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (true){//loads the state of phone call listener
            PhoneCallService.startActionPhoneCallListenerSwitch(context, RecorderApplication.G_PHONE_CALL_LISTENER_ON, "");
        }else {
            PhoneCallService.startActionPhoneCallListenerSwitch(context, RecorderApplication.G_PHONE_CALL_LISTENER_OFF, "");
        }
    }
}

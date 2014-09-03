package com.humming.heqzha.phonecallrecorder.Test;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.humming.heqzha.phonecallrecorder.PhoneCallService;
import com.humming.heqzha.phonecallrecorder.library.FixedSizeQueue;

/**
 * Used to test the performances of Phone Call Service with Phone State Receiver.
 * Created by heqzha on 14-8-30.
 */
public class TestRecordServiceAction {

    // Phone State
    private static final Integer PHONE_STATE_IDLE = 0;
    private static final Integer PHONE_STATE_RINGING = 1;
    private static final Integer PHONE_STATE_OFFHOOK = 2;

    private Context mContext;
    private FixedSizeQueue mPhoneCallStateQ;
    private final String mFakeNum = "123456";


    public TestRecordServiceAction(Context context){
        mContext = context;
        mPhoneCallStateQ = new FixedSizeQueue(9);
    }

    private void fakeOnReceive(Context context, String state, String incomingNumber){
        Log.d("TestRecordServiceAction.fakeOnReceive", "Start");
            Log.d("TestRecordServiceAction.fakeOnReceive", "Phone State: " + state);
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                PhoneCallService.startActionPhoneStateChangedIdle(context, incomingNumber, "");
            }else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
                PhoneCallService.startActionPhoneStateChangedRinging(context, incomingNumber, "");
            }else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)){
                PhoneCallService.startActionPhoneStateChangedOffhook(context, incomingNumber, "");
            }
        fakeOnReceiveFeedback(state);
        Log.d("TestRecordServiceAction.fakeOnReceive", "Done");
    }

    private void fakeOnReceiveFeedback(String state){
        Log.d("TestRecordServiceAction.fakeOnReceiveFeedback", "Start");
        Integer intState = convertStringToInt(state);
        Integer first = mPhoneCallStateQ.poll();
        if (intState.equals(first)){
            Log.d("TestRecordServiceAction.fakeOnReceiveFeedback", "State: " + state + " finished");
        }else{
            Log.e("TestRecordServiceAction.fakeOnReceiveFeedback", "Receive State: " + state + ", Expect State: " + convertIntToString(first));
        }
        Log.d("TestRecordServiceAction.fakeOnReceiveFeedback", "Done");
    }

    private Integer convertStringToInt(String state){
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            return PHONE_STATE_IDLE;
        }else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
            return PHONE_STATE_RINGING;
        }else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)){
            return PHONE_STATE_OFFHOOK;
        }
        return null;
    }

    private String convertIntToString(Integer state){
        if (PHONE_STATE_IDLE.equals(state)){
            return TelephonyManager.EXTRA_STATE_IDLE;
        }else if(PHONE_STATE_RINGING.equals(state)){
            return TelephonyManager.EXTRA_STATE_RINGING;
        }else if (PHONE_STATE_OFFHOOK.equals(state)){
            return TelephonyManager.EXTRA_STATE_OFFHOOK;
        }
        return null;
    }

    public void fakePhoneCallState(){
        Log.d("TestRecordServiceAction.fakePhoneCallState", "Start");
        mPhoneCallStateQ.offer(PHONE_STATE_RINGING);
        mPhoneCallStateQ.offer(PHONE_STATE_OFFHOOK);
        mPhoneCallStateQ.offer(PHONE_STATE_IDLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mPhoneCallStateQ.isEmpty()){
                    String state = convertIntToString(mPhoneCallStateQ.element());
                    fakeOnReceive(mContext, state, mFakeNum);
                }
            }
        }).start();

        Log.d("TestRecordServiceAction.fakePhoneCallState", "Done");
    }
}

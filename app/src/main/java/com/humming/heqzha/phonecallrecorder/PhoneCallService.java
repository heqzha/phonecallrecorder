package com.humming.heqzha.phonecallrecorder;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.humming.heqzha.phonecallrecorder.library.AudioRecordHelper;
import com.humming.heqzha.phonecallrecorder.library.DataBaseHelper;
import com.humming.heqzha.phonecallrecorder.library.FixedSizeQueue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PhoneCallService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_PHONE_CALL_LISTENER_SWITCH = "com.humming.heqzha.phonecallrecorder.action.phone.call.listener.init";
    private static final String ACTION_PHONE_STATE_CHANGED_IDLE = "com.humming.heqzha.phonecallrecorder.action.phone.state.changed.idle";
    private static final String ACTION_PHONE_STATE_CHANGED_OFFHOOK = "com.humming.heqzha.phonecallrecorder.action.phone.state.changed.offhook";
    private static final String ACTION_PHONE_STATE_CHANGED_RINGING = "com.humming.heqzha.phonecallrecorder.action.phone.state.changed.ringing";
    private static final String ACTION_RECORD_STATE_START = "com.humming.heqzha.phonecallrecorder.action.record.state.start";
    private static final String ACTION_RECORD_STATE_END = "com.humming.heqzha.phonecallrecorder.action.record.state.end";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.humming.heqzha.phonecallrecorder.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.humming.heqzha.phonecallrecorder.extra.PARAM2";


    // Phone State
    private static final Integer PHONE_STATE_IDLE = 0;
    private static final Integer PHONE_STATE_RINGING = 1;
    private static final Integer PHONE_STATE_OFFHOOK = 2;


    // Record Service Action
    private static final Integer ACTION_RECORD_IDLE = 0;
    private static final Integer ACTION_RECORD_START = 1;
    private static final Integer ACTION_RECORD_END = 2;


    private static final Integer[] SERVICE_STATE_RECORD_START = {PHONE_STATE_IDLE, PHONE_STATE_RINGING, PHONE_STATE_OFFHOOK};
    private static final Integer[] SERVICE_STATE_RECORD_END = {PHONE_STATE_RINGING, PHONE_STATE_OFFHOOK, PHONE_STATE_IDLE};

    //Phone Call Record Service
    private ServiceBinder mBinder;

    //Store current phone call state information.
    private PhoneStateInfo mCurrentState;

    //The Recorder Application containing the Global Parameters
    private RecorderApplication mRecordApp;
    private FixedSizeQueue mPhoneStateQ;        //Obtained from Application

    //Audio Record Helper and Configuration Parameters
    private AudioRecordHelper mARHelper;
    private Integer mAudioSrc;
    private Integer mOutputFormat;
    private Integer mAudioEncoder;
    private String mOutputFile;

    //Database helper
    private DataBaseHelper mDBHelper;


    public PhoneCallService() {
        super("PhoneCallService");
    }

    public static void startActionPhoneCallListenerSwitch(Context context, String param1, String param2){
        Log.d("PhoneCallService.startActionPhoneCallListenerSwitch", "Start");
        Intent intent = new Intent(context, PhoneCallService.class);
        intent.setAction(ACTION_PHONE_CALL_LISTENER_SWITCH);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
        Log.d("PhoneCallService.startActionPhoneCallListenerSwitch", "End");
    }
    /**
     * Starts this service to perform action phone state changed to idle with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPhoneStateChangedIdle(Context context, String param1, String param2) {
        Log.d("PhoneCallService.startActionPhoneStateChangedIdle", "Start");
        Intent intent = new Intent(context, PhoneCallService.class);
        intent.setAction(ACTION_PHONE_STATE_CHANGED_IDLE);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
        Log.d("PhoneCallService.startActionPhoneStateChangedIdle", "Done");
    }

    /**
     * Starts this service to perform action phone state changed to offhook with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPhoneStateChangedOffhook(Context context, String param1, String param2) {
        Log.d("PhoneCallService.startActionPhoneStateChangedOffhook", "Start");
        Intent intent = new Intent(context, PhoneCallService.class);
        intent.setAction(ACTION_PHONE_STATE_CHANGED_OFFHOOK);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
        Log.d("PhoneCallService.startActionPhoneStateChangedOffhook", "Done");
    }

    /**
     * Starts this service to perform action phone state changed to offhook with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPhoneStateChangedRinging(Context context, String param1, String param2) {
        Log.d("PhoneCallService.startActionPhoneStateChangedRinging", "Start");
        Intent intent = new Intent(context, PhoneCallService.class);
        intent.setAction(ACTION_PHONE_STATE_CHANGED_RINGING);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
        Log.d("PhoneCallService.startActionPhoneStateChangedRinging", "Done");
    }

    /**
     * Starts this service to perform action record starting with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRecordStart(Context context, String param1, String param2) {
        Log.d("PhoneCallService.startActionRecordStart", "Start");
        Intent intent = new Intent(context, PhoneCallService.class);
        intent.setAction(ACTION_RECORD_STATE_START);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
        Log.d("PhoneCallService.startActionRecordStart", "Done");
    }

    /**
     * Starts this service to perform action record ending with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRecordEnd(Context context, String param1, String param2) {
        Log.d("PhoneCallService.startActionRecordEnd", "Start");
        Intent intent = new Intent(context, PhoneCallService.class);
        intent.setAction(ACTION_RECORD_STATE_END);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
        Log.d("PhoneCallService.startActionRecordEnd", "Done");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PhoneCallService.onCreate", "Start");
        //TODO:Read Setting File

        //TODO:Initialize Phone Call Record Service
        mBinder = new ServiceBinder();
        mRecordApp = (RecorderApplication)getApplication();
        mPhoneStateQ = mRecordApp.getPhoneStateQueue();

        //TODO:Initialize Audio Record Helper(get from setting file)
        mARHelper = new AudioRecordHelper();
        mAudioSrc = MediaRecorder.AudioSource.MIC;
        mOutputFormat = MediaRecorder.OutputFormat.MPEG_4;
        mAudioEncoder = MediaRecorder.AudioEncoder.AMR_NB;
        mOutputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PhoneCallRecorder/";

        //TODO:Initialize Database Helper
        mDBHelper = new DataBaseHelper(this);
        Log.d("PhoneCallService.onCreate", "Done");
    }

    @Override
    public void onDestroy() {
        Log.d("PhoneCallService.onDestroy", "Start");
        //Save temporary data
        mRecordApp.setPhoneStateQueue(mPhoneStateQ);
        mDBHelper.close();
        Log.d("PhoneCallService.onDestroy", "Done");
    }

    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
        Log.d("PhoneCallService.onStartCommand", "Start");
        super.onStartCommand(intent, flags, startId);
        Log.i("PhoneCallService.onStartCommand", "Received start id " + startId + ": " + intent);
        Log.d("PhoneCallService.onStartCommand", "Done");
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("PhoneCallService.onHandleIntent", "Start");
        if (intent != null) {
            final String action = intent.getAction();
            final String param1 = intent.getStringExtra(EXTRA_PARAM1);
            final String param2 = intent.getStringExtra(EXTRA_PARAM2);
            if (ACTION_PHONE_CALL_LISTENER_SWITCH.equals(action)){
                handleActionPhoneCallListenerSwitch(param1,param2);
            }else if(ACTION_PHONE_STATE_CHANGED_IDLE.equals(action)) {
                handleActionPhoneStateChangedIdle(param1, param2);
            } else if (ACTION_PHONE_STATE_CHANGED_OFFHOOK.equals(action)) {
                handleActionPhoneStateChangedOffhook(param1, param2);
            } else if (ACTION_PHONE_STATE_CHANGED_RINGING.equals(action)) {
                handleActionPhoneStateChangedRinging(param1, param2);
            } else if (ACTION_RECORD_STATE_START.equals(action)){
                handleActionRecordStart(param1, param2);
            } else if (ACTION_RECORD_STATE_END.equals(action)){
                handleActionRecordEnd(param1, param2);
            }
        }
        Log.d("PhoneCallService.onHandleIntent", "Done");
    }

    private void handleActionPhoneCallListenerSwitch(String param1, String param2){
        Log.d("PhoneCallService.handleActionPhoneStateChangedIdle", "Start");
        if (RecorderApplication.G_PHONE_CALL_LISTENER_ON.equals(param1)){
            //  Active Phone State Receiver
            PackageManager pm  = this.getPackageManager();
            ComponentName componentName = new ComponentName(this, PhoneStateReceiver.class);
            pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            Toast.makeText(getApplicationContext(), "Phone Call Recorder On", Toast.LENGTH_LONG).show();
            Log.d("PhoneCallService.handleActionPhoneStateChangedIdle", "Phone Call Listener On");
        }else {
            //  Cancel Phone State Receiver
            PackageManager pm  = this.getPackageManager();
            ComponentName componentName = new ComponentName(this, PhoneStateReceiver.class);
            pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            Toast.makeText(getApplicationContext(), "Phone Call Recorder Off", Toast.LENGTH_LONG).show();
            Log.d("PhoneCallService.handleActionPhoneStateChangedIdle", "Phone Call Listener Off");
        }
        Log.d("PhoneCallService.handleActionPhoneStateChangedIdle", "End");
    }
    /**
     * Handle action phone state changed to idle in the provided background thread with
     * the provided parameters.
     * @param param1 phone number
     * @param param2 call state
     */
    private void handleActionPhoneStateChangedIdle(String param1, String param2) {
        Log.d("PhoneCallService.handleActionPhoneStateChangedIdle", "Start");
        mPhoneStateQ.offer(PHONE_STATE_IDLE);
        handleRecordAction(param1, param2);
//        Integer action = getRecordServiceAction();
//        Log.d("PhoneCallService.handleActionPhoneStateChangedIdle",
//                "Service Action: " + action);
        Log.d("PhoneCallService.handleActionPhoneStateChangedIdle",
                "Done");
    }

    /**
     * Handle action phone state changed to offhook in the provided background thread with
     * the provided parameters.
     * @param param1 phone number
     * @param param2 call state
     */
    private void handleActionPhoneStateChangedOffhook(String param1, String param2) {
        Log.d("PhoneCallService.handleActionPhoneStateChangedOffhook", "Start");
        mPhoneStateQ.offer(PHONE_STATE_OFFHOOK);
        handleRecordAction(param1, param2);
//        Integer action = getRecordServiceAction();
//        Log.d("PhoneCallService.handleActionPhoneStateChangedOffhook",
//                "Service Action: " + action);
        Log.d("PhoneCallService.handleActionPhoneStateChangedOffhook",
                "Done");
    }

    /**
     * Handle action phone state changed to ringing in the provided background thread with
     * the provided parameters.
     * @param param1 phone number
     * @param param2 call state
     */
    private void handleActionPhoneStateChangedRinging(String param1, String param2) {
        Log.d("PhoneCallService.handleActionPhoneStateChangedRinging", "Start");
        mPhoneStateQ.offer(PHONE_STATE_RINGING);
        handleRecordAction(param1, param2);

//        Integer action = getRecordServiceAction();
//        Log.d("PhoneCallService.handleActionPhoneStateChangedRinging",
//                "Service Action: " + action);
        Log.d("PhoneCallService.handleActionPhoneStateChangedRinging",
                "Done");
    }

    public class PhoneStateInfo{
        public String mNumber;
        public String mName;
        public String mCallState;
        public String mBeginTime;
        public String mEndTime;
        public String mFile;

        public PhoneStateInfo(){
            reset();
        }

        public void reset(){
            mNumber = "";
            mName = "";
            mCallState = "";
            mBeginTime = "";
            mEndTime = "";
            mFile = "";
        }
    }

    /**
     * Handle action record starting in the provided background thread with
     * the provided parameters.
     * @param param1 phone number
     * @param param2 call state
     */
    private void handleActionRecordStart(String param1, String param2) {
        Log.d("PhoneCallService.handleActionRecordStart", "Start");
        mCurrentState = new PhoneStateInfo();
        mCurrentState.mNumber = param1;
        mCurrentState.mCallState = param2;
        Calendar c = Calendar.getInstance();
        mCurrentState.mBeginTime = RecorderApplication.DATAFORMAT.format(c.getTime());
        mCurrentState.mFile = mARHelper.startRecording(mAudioSrc, mOutputFormat, mAudioEncoder, mOutputFile);
        Log.d("PhoneCallService.handleActionRecordStart",
                "Done");
    }

    /**
     * Handle action record ending in the provided background thread with
     * the provided parameters.
     * @param param1 phone number
     * @param param2 call state
     */
    private void handleActionRecordEnd(String param1, String param2) {
        Log.d("PhoneCallService.handleActionRecordEnd", "Start");
        mARHelper.endRecording();
        if (!mCurrentState.mNumber.equals(param1)){
            Log.e("PhoneCallService.handleActionRecordEnd", "Phone Number is not matched. Expect "
                    + param2 + " " + mCurrentState.mNumber+ " but comes "+param1);
            return;
        }
        mCurrentState.mName = "Name_" + param1;//TODO: Get from contacts.

        Calendar c = Calendar.getInstance();
        mCurrentState.mEndTime = RecorderApplication.DATAFORMAT.format(c.getTime());
        ArrayList<String> list = addToList(mCurrentState);
        try {
            mDBHelper.insert(list);
        } catch (ParseException e) {
            Log.e("RecordingService.handleActionRecordEnd", "Insert a new record failed.");
            e.printStackTrace();
        }
        Log.d("PhoneCallService.handleActionRecordEnd",
                "Done");
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.d("PhoneCallService.onBind", "Done");
        return mBinder;
    }

    public class ServiceBinder extends Binder {
        public PhoneCallService getService() {
            Log.d("PhoneCallService.ServiceBinder.getService", "Done");
            return PhoneCallService.this;
        }
    }

    private void handleRecordAction(String number, String callState){
        Log.d("PhoneCallService.handleRecordAction", "Start");
        Integer action = getRecordServiceAction();
        if (ACTION_RECORD_IDLE.equals(action)){
            Log.d("PhoneCallService.handleRecordAction", "Recording Idle");
        }else if(ACTION_RECORD_START.equals(action)){
            Log.d("PhoneCallService.handleRecordAction", "Recording Start");
            PhoneCallService.startActionRecordStart(this, number, callState);
        }else if (ACTION_RECORD_END.equals(action)){
            Log.d("PhoneCallService.handleRecordAction", "Recording End");
            PhoneCallService.startActionRecordEnd(this, number, callState);
        }else {
            Log.w("PhoneCallService.handleRecordAction", "Recording Action Unknown");
        }
        Log.d("PhoneCallService.handleRecordAction", "End");
    }

    private Integer getRecordServiceAction(){
        Log.d("PhoneCallService.getRecordServiceAction", "Start");
        Integer isRecordStart = 0, isRecordEnd = 0;
        Integer[] currentState = new Integer[mPhoneStateQ.size()];
        currentState = mPhoneStateQ.toArray(currentState);

        for (int i = 0; i < currentState.length; ++i){
            if (currentState[i].equals(SERVICE_STATE_RECORD_START[i])){
                ++isRecordStart;
            } else if(currentState[i].equals(SERVICE_STATE_RECORD_END[i])){
                ++isRecordEnd;
            }
        }
        Integer action;
        if (isRecordStart == SERVICE_STATE_RECORD_START.length){
            //Idle, Ring, Offhook
            action = ACTION_RECORD_START;
        }else if (isRecordEnd == SERVICE_STATE_RECORD_END.length){
            //Ring, Offhook, Idle
            action = ACTION_RECORD_END;
        }else {
            //Others
            action = ACTION_RECORD_IDLE;
        }
        Log.d("PhoneCallService.getRecordServiceAction", "Action: "+action);
        Log.d("PhoneCallService.getRecordServiceAction", "Done");
        return action;
    }

    private ArrayList<String> addToList(PhoneStateInfo info){
        ArrayList<String> list = new ArrayList<String>();
        list.add(Integer.toString(Integer.parseInt(mDBHelper.getId()) + 1));
        list.add(info.mName);
        list.add(info.mNumber);
        list.add(info.mCallState);
        list.add(info.mBeginTime);
        list.add(info.mEndTime);
        list.add(info.mFile);
        return list;
    }
}


package com.humming.heqzha.phonecallrecorder;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.humming.heqzha.phonecallrecorder.Test.TestPhoneStateReceiver;
import com.humming.heqzha.phonecallrecorder.library.DataBaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RecordMainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Phone Call Record Service
     */
    private PhoneCallService mPCService;

    /**
     * Used to bind with Service.
     */
    private ServiceConnection mConnectPhoneCallServ;

    /**
     * UI Components
     */
//    private ExpandableListAdapter mListAdapter;
    private ArrayAdapter<String> mListAdapter;
    private List<String> mList;
    private Switch mServiceSwitch;

    //Database helper
    private DataBaseHelper mDBHelper;
    private List<String> mIdList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Log.d("RecordMainActivity.onCreate", "Start");
        //TODO: Read Setting file

        //Initialize Database Helper
        mDBHelper = new DataBaseHelper(this);
        mIdList = new ArrayList<String>();

        //Initialize UI Components
        mList = new ArrayList<String>();
        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mList);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(mListAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO
                String id = mIdList.get(i);
                ArrayList<String> info = mDBHelper.getInfoById(id);
                String filePath = info.get(DataBaseHelper.RECORD_INFO_FILE_PATH_INDEX);
                Log.d("RecordMainActivity.ListView.onItemClick", "ID: " + id + " File: " +
                        filePath);

                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(filePath);
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String Id = mIdList.get(i);
                delDialog(Id);
                return false;
            }

            private void delDialog(final String id){
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordMainActivity.this);
                builder.setMessage("Delete the record " + id + "?");
                builder.setTitle("Notice");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<String> info = mDBHelper.getInfoById(id);
                        String filePath = info.get(DataBaseHelper.RECORD_INFO_FILE_PATH_INDEX);
                        Log.d("RecordMainActivity.ListView.setOnItemLongClickListener.onItemLongClick", "ID: " + id + " File: " +
                                filePath);
                        File file = new File(filePath);

                        if (file.delete()){
                            mDBHelper.delete(id);
                        }else{
                            Log.e("RecordMainActivity.ListView.setOnItemLongClickListener.onItemLongClick"
                                    , "Delete "+filePath+" failed!");
                        }
                        updateRecordList();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        mServiceSwitch = (Switch) findViewById(R.id.service_switch);
        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //  Active Phone State Receiver
                    PackageManager pm  = RecordMainActivity.this.getPackageManager();
                    ComponentName componentName = new ComponentName(RecordMainActivity.this, PhoneStateReceiver.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                    Toast.makeText(getApplicationContext(), "activated", Toast.LENGTH_LONG).show();

                }else {
                    //  Cancel Phone State Receiver
                    PackageManager pm  = RecordMainActivity.this.getPackageManager();
                    ComponentName componentName = new ComponentName(RecordMainActivity.this, PhoneStateReceiver.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Initialize Service Connection
        mConnectPhoneCallServ = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d("RecordMainActivity.mConnectPhoneCallServ.onServiceConnected", "Start");
                PhoneCallService.ServiceBinder binder = (PhoneCallService.ServiceBinder) iBinder;
                mPCService = binder.getService();
                Log.d("RecordMainActivity.mConnectPhoneCallServ.onServiceConnected", "Done");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("RecordMainActivity.mConnectPhoneCallServ.onServiceConnected", "Start");
                mPCService = null;
                Log.d("RecordMainActivity.mConnectPhoneCallServ.onServiceConnected", "Done");
            }
        };

        Log.d("RecordMainActivity.onCreate", "Done");

    }

    @Override
    protected void onResume(){
        super.onResume();
        //TODO:Restore last status
        Log.d("RecordMainActivity.onResume", "Start");
        Intent intentPCS= new Intent(this, PhoneCallService.class);
        bindService(intentPCS, mConnectPhoneCallServ,
                Context.BIND_AUTO_CREATE);
        updateRecordList();
        Log.d("RecordMainActivity.onResume", "Done");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO: Save current status
        Log.d("RecordMainActivity.onPause", "Start");
        unbindService(mConnectPhoneCallServ);
        Log.d("RecordMainActivity.onPause", "Done");
    }

    public void onClick_Ringing_Button(View view){
        Log.d("RecordMainActivity.onClick_Ringing_Button", "Start");
        TestPhoneStateReceiver.testSendAction(this, TelephonyManager.EXTRA_STATE_RINGING);
        Log.d("RecordMainActivity.onClick_Ringing_Button", "Done");
    }

    public void onClick_Offhook_Button(View view){
        Log.d("RecordMainActivity.onClick_Offhook_Button", "Start");
        TestPhoneStateReceiver.testSendAction(this, TelephonyManager.EXTRA_STATE_OFFHOOK);
        Log.d("RecordMainActivity.onClick_Offhook_Button", "Done");
    }

    public void onClick_Idle_button(View view){
        Log.d("RecordMainActivity.onClick_Idle_button", "Start");
        TestPhoneStateReceiver.testSendAction(this, TelephonyManager.EXTRA_STATE_IDLE);
        Log.d("RecordMainActivity.onClick_Idle_button", "Done");
    }

    public void updateRecordList(){
        ArrayList<ArrayList<String>> allStateInfo = mDBHelper.getAllInfo();
        ArrayList<String> numberList = new ArrayList<String>();
        ArrayList<String> idList = new ArrayList<String>();

        for (ArrayList<String> info : allStateInfo) {
            idList.add(info.get(DataBaseHelper.RECORD_INFO_ID_INDEX));
            numberList.add(info.get(DataBaseHelper.RECORD_INFO_NUMBER_INDEX));
        }
        mIdList.clear();
        mIdList.addAll(idList);

        mList.clear();
        mList.addAll(numberList);
        mListAdapter.notifyDataSetChanged();
    }
//
//    private boolean toggle = true;
//    public void onClick_Update_Button(View view){
//        Log.d("RecordMainActivity.onClick_Update_Button", "Start");
//        TestPhoneStateReceiver.shuffleArray(actions);
//        for (int i = 0; i < actions.length; ++i){
//            TestPhoneStateReceiver.testSendAction(this, actions[i]);
//        }
////        if (toggle){
////            PhoneCallService.startActionRecordStart(this, "123456", "Incoming");
////        }else {
////            PhoneCallService.startActionRecordEnd(this,"123456","Incoming");
////        }
////        toggle = !toggle;
//
//
//        Log.d("RecordMainActivity.onClick_Update_Button", "Done");
//    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.record_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_record_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((RecordMainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}

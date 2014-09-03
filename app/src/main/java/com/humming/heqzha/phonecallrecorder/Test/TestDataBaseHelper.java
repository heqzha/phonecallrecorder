package com.humming.heqzha.phonecallrecorder.Test;

import android.content.Context;
import android.util.Log;

import com.humming.heqzha.phonecallrecorder.library.DataBaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Used to test Database Helper.
 * Created by heqzha on 14-8-27.
 */
public class TestDataBaseHelper {
    private DataBaseHelper mDBHelper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TestDataBaseHelper(Context context){
        mDBHelper = new DataBaseHelper(context);
    }

    public void testInsert(int _id){
        ArrayList<String> list = new ArrayList<String>();
        String id = Integer.toString(_id);
        list.add(id);
        list.add("Name_" + id);
        list.add("Number_" + id);
        list.add((_id%2==0)?"Call in":"Call out");//Call state: 0->call in, 1->call out
        Calendar c = Calendar.getInstance();
        list.add(dateFormat.format(c.getTime()));
        c.add(Calendar.MINUTE, 10);
        list.add(dateFormat.format(c.getTime()));
        list.add("FILEPATH/" + id + ".mp4");
        c = Calendar.getInstance();
        list.add(dateFormat.format(c.getTime()));
        try {
            mDBHelper.insert(list);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void testGetInfoById(int _id){
        ArrayList<String> list = mDBHelper.getInfoById(Integer.toString(_id));
        printOut(list);
    }
    public void testGetAllInfo(){
        ArrayList<ArrayList<String>> all = mDBHelper.getAllInfo();
        for (ArrayList<String> list : all) {
            printOut(list);
        }
    }

    public void testDeleteById(String _id){
        mDBHelper.delete(_id);
    }
    public void testDeleteTable(){
        mDBHelper.resetDataBase();
    }
    public void printOut(ArrayList<String> list){
        Log.d(DataBaseHelper.RECORD_INFO_ID_FIELD, list.get(DataBaseHelper.RECORD_INFO_ID_INDEX));
        Log.d(DataBaseHelper.RECORD_INFO_NAME_FIELD, list.get(DataBaseHelper.RECORD_INFO_NAME_INDEX));
        Log.d(DataBaseHelper.RECORD_INFO_NUMBER_FIELD, list.get(DataBaseHelper.RECORD_INFO_NUMBER_INDEX));
        Log.d(DataBaseHelper.RECORD_INFO_CALL_STATE_FIELD, list.get(DataBaseHelper.RECORD_INFO_CALL_STATE_INDEX));
        Log.d(DataBaseHelper.RECORD_INFO_BEGIN_TIME_FIELD, list.get(DataBaseHelper.RECORD_INFO_BEGIN_TIME_INDEX));
        Log.d(DataBaseHelper.RECORD_INFO_END_TIME_FIELD, list.get(DataBaseHelper.RECORD_INFO_END_TIME_INDEX));
        Log.d(DataBaseHelper.RECORD_INFO_FILE_PATH_FIELD, list.get(DataBaseHelper.RECORD_INFO_FILE_PATH_INDEX));
        Log.d(DataBaseHelper.RECORD_INFO_CREATE_TIME_FIELD, list.get(DataBaseHelper.RECORD_INFO_CREATE_TIME_INDEX));
    }


}
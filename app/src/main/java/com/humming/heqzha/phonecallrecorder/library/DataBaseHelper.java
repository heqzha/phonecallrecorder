package com.humming.heqzha.phonecallrecorder.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.humming.heqzha.phonecallrecorder.RecorderApplication;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Defines a helper used to handle the operations for database.
 * Created by heqzha on 14-8-26.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    //Table name
    private final static String DATABASE_NAME = "PhoneRecordInfo.db";
    //Data base version
    private static int DATABASE_VERSION=1;
    //Table name
    public final static String RECORD_INFO_TABLE_NAME = "record_info_tb";

    //Record info field
    public final static String RECORD_INFO_ID_FIELD = "record_info_id"; //Main key
    public final static String RECORD_INFO_NAME_FIELD = "record_info_name";
    public final static String RECORD_INFO_NUMBER_FIELD = "record_info_number";
    public final static String RECORD_INFO_CALL_STATE_FIELD = "record_info_call_state";//call in/call out
    public final static String RECORD_INFO_BEGIN_TIME_FIELD = "record_info_begin_time";
    public final static String RECORD_INFO_END_TIME_FIELD = "record_info_end_time";
    public final static String RECORD_INFO_FILE_PATH_FIELD = "record_info_file_path";
    public final static String RECORD_INFO_CREATE_TIME_FIELD = "record_info_create_time";

    //Record info field
    public final static int RECORD_INFO_ID_INDEX = 0; //Main key
    public final static int RECORD_INFO_NAME_INDEX = 1;
    public final static int RECORD_INFO_NUMBER_INDEX = 2;
    public final static int RECORD_INFO_CALL_STATE_INDEX = 3;//call in/call out
    public final static int RECORD_INFO_BEGIN_TIME_INDEX = 4;
    public final static int RECORD_INFO_END_TIME_INDEX = 5;
    public final static int RECORD_INFO_FILE_PATH_INDEX = 6;
    public final static int RECORD_INFO_CREATE_TIME_INDEX = 7;

    //Get object of read and write
    private SQLiteDatabase mReadDb=this.getReadableDatabase();
    private SQLiteDatabase mWriteDb=this.getWritableDatabase();

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create Table
        String sql;
        sql = "CREATE TABLE " + RECORD_INFO_TABLE_NAME
                +" ( "
                +RECORD_INFO_ID_FIELD+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +RECORD_INFO_NAME_FIELD+" TEXT, "
                +RECORD_INFO_NUMBER_FIELD+" TEXT NOT NULL, "
                +RECORD_INFO_CALL_STATE_FIELD+" TEXT NOT NULL, "
                + RECORD_INFO_BEGIN_TIME_FIELD +" TEXT NOT NULL, "
                +RECORD_INFO_END_TIME_FIELD+" TEXT NOT NULL, "
                +RECORD_INFO_FILE_PATH_FIELD+" TEXT, "
                + RECORD_INFO_CREATE_TIME_FIELD +" DATETIME NOT NULL "
                +")";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //When version changed, Reset Database
        String sql = " DROP TABLE IF EXISTS " + RECORD_INFO_TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public void resetDataBase(){
        //Change version for reset Database
        mWriteDb.delete(RECORD_INFO_TABLE_NAME, null, null);
    }

    public ArrayList<ArrayList<String>> getAllInfo(){
        ArrayList<ArrayList<String>> allInfo = new ArrayList<ArrayList<String>>();
        SQLiteDatabase db= this.mReadDb;
        Cursor cursor;
        cursor = db.query(RECORD_INFO_TABLE_NAME, null, null,
                null, null, null,
                RECORD_INFO_ID_FIELD + "  ASC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < cursor.getColumnCount(); ++i){
                    list.add(String.valueOf(cursor
                            .getString(i)));
                }
                allInfo.add(list);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return allInfo;
    }

    public ArrayList<String> getInfoById(String id){
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db= this.mReadDb;
        Cursor cursor;
        cursor = db.query(RECORD_INFO_TABLE_NAME, null, RECORD_INFO_ID_FIELD + "=?",
                new String[] {id}, null, null,
                RECORD_INFO_ID_FIELD + "  ASC");
        if (cursor != null){
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getColumnCount(); ++i){
                list.add(String.valueOf(cursor
                        .getString(i)));
            }
            cursor.close();
        }
        return list;
    }

    public long insert(ArrayList<String> dataList) throws ParseException {
        //Insert data by table name and data
        ContentValues cv=new ContentValues();
        cv.put(RECORD_INFO_ID_FIELD, dataList.get(RECORD_INFO_ID_INDEX));
        cv.put(RECORD_INFO_NAME_FIELD, dataList.get(RECORD_INFO_NAME_INDEX));
        cv.put(RECORD_INFO_NUMBER_FIELD, dataList.get(RECORD_INFO_NUMBER_INDEX));
        cv.put(RECORD_INFO_CALL_STATE_FIELD, dataList.get(RECORD_INFO_CALL_STATE_INDEX));
        cv.put(RECORD_INFO_BEGIN_TIME_FIELD, dataList.get(RECORD_INFO_BEGIN_TIME_INDEX  ));
        cv.put(RECORD_INFO_END_TIME_FIELD, dataList.get(RECORD_INFO_END_TIME_INDEX));
        cv.put(RECORD_INFO_FILE_PATH_FIELD, dataList.get(RECORD_INFO_FILE_PATH_INDEX));

        Calendar c = Calendar.getInstance();
        String currentTime = RecorderApplication.DATAFORMAT.format(c.getTime());

        cv.put(RECORD_INFO_CREATE_TIME_FIELD, currentTime);

        SQLiteDatabase db= this.mWriteDb;
        return db.insert(RECORD_INFO_TABLE_NAME, null, cv);
    }

    public void delete(String id)
    {
        //Delete data
        SQLiteDatabase db = this.mWriteDb;
        String where=RECORD_INFO_ID_FIELD+"=?";
        String[] whereValue={id};
        db.delete(RECORD_INFO_TABLE_NAME, where, whereValue);
    }

    public String getId(){
        //Return quiz id which is the largest
        SQLiteDatabase db = this.mReadDb;
        Cursor cursor = db.query(RECORD_INFO_TABLE_NAME,
                new String[] { RECORD_INFO_ID_FIELD }, null,
                null, null, null, RECORD_INFO_ID_FIELD + " DESC");
        cursor.moveToFirst();
        String id = "0";
        if (cursor.getCount() > 0){
            id = String.valueOf(cursor.getInt(0));
        }

        cursor.close();
        return id;
    }

}

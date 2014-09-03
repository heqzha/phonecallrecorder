package com.humming.heqzha.phonecallrecorder.Test;

import android.media.MediaRecorder;
import android.os.Environment;

import com.humming.heqzha.phonecallrecorder.library.AudioRecordHelper;

/**
 * Used to test audio recorder.
 * Created by heqzha on 14-8-31.
 */
public class TestMediaRecorder {
    private AudioRecordHelper recordHelper;

    public TestMediaRecorder(){
        recordHelper = new AudioRecordHelper();
    }

    public String testRecordStart(){
        return recordHelper.startRecording(MediaRecorder.AudioSource.MIC,
                MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AMR_NB,
                Environment.getExternalStorageDirectory().getAbsolutePath()+"/");
    }

    public boolean testRecordEnd(){
        return recordHelper.endRecording();
    }
}

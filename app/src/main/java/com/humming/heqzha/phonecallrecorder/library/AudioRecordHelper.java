package com.humming.heqzha.phonecallrecorder.library;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * AudioRecordHelper is used to record audios and output in files.
 * Created by heqzha on 14-8-31.
 */
public class AudioRecordHelper {

    private final SimpleDateFormat DATAFORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    private MediaRecorder mRecorder = null;

    public AudioRecordHelper(){
        mRecorder = new MediaRecorder();
    }

    /**
     * Setting parameters of the Media Recorder and starting it.
     * @param audioSrc defines audio source.
     * @param outputFormat defines output format.
     * @param audioEncoder defines audio encoder.
     * @param outputFile defines the path of output file.
     * @return output file path.
     */
    public String startRecording(Integer audioSrc, Integer outputFormat, Integer audioEncoder, String outputFile){
        Log.d("AudioRecordHelper.startRecording", "Start");
        mRecorder.setAudioSource(audioSrc);
        mRecorder.setOutputFormat(outputFormat);
        mRecorder.setAudioEncoder(audioEncoder);

        Calendar c = Calendar.getInstance();
        String currentTime = DATAFORMAT.format(c.getTime());

        File folder = new File(outputFile);
        if (!folder.isDirectory()){
            if (!folder.mkdirs()){
                Log.e("RecordingService.handleActionRecordStart", "Create folder(s) " +
                        folder.getAbsolutePath() + " failed.");
            }
        }

        String filePath = outputFile + currentTime + getFileSuffix(outputFormat);
        File file = new File(filePath);

        if(file.exists()) {
            if(!file.delete()){
                Log.e("RecordingService.handleActionRecordStart", "Delete file " +
                        file.getAbsolutePath() + "failed.");
            }
        }

        mRecorder.setOutputFile(file.getAbsolutePath());

        try {
            mRecorder.prepare();
        } catch (Exception e) {
            Log.e("RecordingService.handleActionRecordStart", "Prepare recorder failed.");
            e.printStackTrace();
            return null;
        }
        mRecorder.start();
        Log.d("AudioRecordHelper.startRecording", "Done");
        return filePath;
    }

    /**
     * Stops and releases recorder.
     * @return true if recording is stopped successfully.
     */
    public boolean endRecording(){
        Log.d("AudioRecordHelper.endRecording", "Start");
        try {
            mRecorder.stop();
        }catch (IllegalStateException e){
            Log.e("AudioRecordHelper.endRecording", "Stop recording failed.");
            e.getStackTrace();
            return false;
        }

        mRecorder.release();
        Log.d("AudioRecordHelper.endRecording", "Done");
        return true;
    }

    /**
     * Get file suffix by using output format
     * @param outputFormat defines output format
     * @return file suffix
     */
    private String getFileSuffix(Integer outputFormat){
        if (outputFormat.equals(MediaRecorder.OutputFormat.MPEG_4)){
            return ".mp4";
        } else if (outputFormat.equals(MediaRecorder.OutputFormat.THREE_GPP)){
            return ".3gp";
        } else {
            return "";
        }
    }
}

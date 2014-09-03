package com.humming.heqzha.phonecallrecorder.Test;

import android.util.Log;

import com.humming.heqzha.phonecallrecorder.library.FixedSizeQueue;

/**
 * Used to test Fixed Queue.
 * Created by heqzha on 14-8-30.
 */
public class TestFixedQueue {
    private FixedSizeQueue mFixedSizeQueue;

    public TestFixedQueue(){
        mFixedSizeQueue = new FixedSizeQueue(5);
    }

    public void testOffer(){
        Log.d("TestFixedQueue.testOffer", "Start");
        for (int i = 0; i < 10; ++i){
            //Log.d("TestFixedQueue.testOffer", Integer.toString(i));
            mFixedSizeQueue.offer(i);
            testPeek();
        }
        Log.d("TestFixedQueue.testOffer", "Done");
    }

    public void testToArray(){
        Log.d("TestFixedQueue.testToArray", "Start");
        Integer[] array = new Integer[mFixedSizeQueue.size()];
        array = mFixedSizeQueue.toArray(array);

        for (Integer anArray : array) {
            Log.d("TestFixedQueue.testToArray", Integer.toString(anArray));
        }

        Log.d("TestFixedQueue.testToArray", "Done");
    }

    public void testPoll(){
        Log.d("TestFixedQueue.testPoll", "Start");
        while (!mFixedSizeQueue.isEmpty()){
            Log.d("TestFixedQueue.testPoll", Integer.toString(mFixedSizeQueue.poll()));
        }
        Log.d("TestFixedQueue.testPoll", "Done");
    }

    public void testPeek(){
        Log.d("TestFixedQueue.testPeek", "Start");
        Log.d("TestFixedQueue.testPeek", Integer.toString(mFixedSizeQueue.peek()));
        Log.d("TestFixedQueue.testPeek", "Done");
    }
}

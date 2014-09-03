package com.humming.heqzha.phonecallrecorder.library;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * Defines a queue which has fixed length.
 * For each new element pushed in, the first element is popped out.
 * Created by heqzha on 14-8-30.
 */
public class FixedSizeQueue implements Queue<Integer> {
    private ArrayList<Integer> mContainer;
    private Integer mContainerSize;

    public FixedSizeQueue(){
        mContainer = new ArrayList<Integer>();
        mContainerSize = 0;
    }

    public FixedSizeQueue(int size){
        mContainer = new ArrayList<Integer>();
        for (int i = 0; i < size; ++i){
            mContainer.add(0);
        }
        mContainerSize = size;
    }
    @Override
    public boolean add(Integer integer) {
        return mContainer.size() < mContainerSize && mContainer.add(integer);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> integers) {
        mContainer.addAll(integers);

        while (mContainer.size() >= mContainerSize){
            mContainer.remove(0);
        }

        return false;
    }

    @Override
    public void clear() {
        mContainer.clear();
    }

    @Override
    public boolean contains(Object o) {
        return mContainer.contains(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> objects) {
        return mContainer.containsAll(objects);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Integer){
            return mContainer.equals(o);
        }
        Log.e("FixedSizeQueue.equals", "Wrong type of parameters");
        return false;
    }

    @Override
    public int hashCode() {
        return mContainer.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return mContainer.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<Integer> iterator() {
        return mContainer.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return mContainer.remove(o);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> objects) {
        return mContainer.removeAll(objects);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> objects) {
        return mContainer.retainAll(objects);
    }

    @Override
    public int size() {
        return mContainer.size();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mContainer.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] ts) {
        int size = mContainer.size();
        T[] r = ts.length >= size?
                ts:(T[]) Array.newInstance(ts.getClass().getComponentType(), size);
        Iterator<Integer> it = mContainer.iterator();
        for(int i = 0; i < r.length; ++i){
            if (!it.hasNext()){
                if (ts != r){
                    return Arrays.copyOf(r,i);
                }
                r[i] = null;
                return r;
            }
            r[i] = (T) it.next();
        }
        return r;
    }

    @Override
    public boolean offer(Integer integer) {
        if (mContainer.size()>=mContainerSize){
            mContainer.remove(0);
        }
        mContainer.add(integer);
        return true;
    }

    @Override
    public Integer remove() {
        return mContainer.remove(0);
    }

    @Override
    public Integer poll() {
        if (mContainer.isEmpty()){
            return null;
        }
        Integer first = mContainer.get(0);
        mContainer.remove(0);
        return first;
    }

    @Override
    public Integer element() {
        return mContainer.get(0);
    }

    @Override
    public Integer peek() {
        if (mContainer.isEmpty()){
            return null;
        }
        return mContainer.get(0);
    }

    public Integer fixedSize(){
        return mContainerSize;
    }
}
